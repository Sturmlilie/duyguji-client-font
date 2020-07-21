#!/bin/ruby

require 'cairo'
require 'json'
require 'zip'

def blit_png(cr, png, cp_writer)
    png_fn = png[:path]
    return unless FileTest.exist?(png_fn)
    surf = Cairo::ImageSurface.from_png(png_fn)
    cr.set_source(surf)
    cr.paint
    surf.finish

    cp_writer.collect png[:code] if not cp_writer.nil?
end

# in_png_ary: { :path => "path/to/file.png", :code => ArbitraryType}
# the :code values are collected for all paths that were successfully opened by cairo
# out_atlas_fn: Full filename of the atlas png to be written
# stride: width of the atlas, in symbols
# in_size: size (width and height) of the input files
# cp_writer: CpMapWriter instance
def make_atlas(in_png_ary, out_atlas_file, stride, in_size, cp_writer)
    atlas_width = stride * in_size
    atlas_height_sym = in_png_ary.size / stride
    atlas_height_sym += 1 if in_png_ary.size % stride > 0
    atlas_height = atlas_height_sym * in_size
    Cairo::ImageSurface::new(atlas_width, atlas_height) do |atlas|
        Cairo::Context.new(atlas) do |cr|
            # in 'emoji-width' units
            x = 0
            y = 0
            in_png_ary.each do |png_fn|
                cr.identity_matrix
                cr.translate(x*in_size, y*in_size)
                blit_png(cr, png_fn, cp_writer)
                x += 1
                if (x >= stride)
                    x = 0
                    y += 1
                end
            end
        end

        atlas.write_to_png(out_atlas_file)
    end
end

class Provider
    # name: String identifier (lowercase), also serves as namespace
    # path: Path to directory containing prerendered png
    # size: Width/height of pngs in @path
    # cp2fn: Format string that allows "% codepoint" expression to get filename without extension
    # fn2cp: Proc that takes in a filename from @path and returns array of codepoints
    # highrespath: Analog to @path, but in a high resolution (for pack.png etc)
    def initialize(name, path, size, cp2fn, fn2cp, highrespath)
        @path = path
        @size = size
        @name = name
        @cp2fn = cp2fn
        @fn2cp = fn2cp
        @highrespath = highrespath
    end

    attr_reader :name

    def codepoint_path(cp)
        "#{@path}/#{@cp2fn % cp}.png"
    end

    def highres_codepoint_path(cp)
        "#{@highrespath}/#{@cp2fn % cp}.png"
    end

    # Run block for each single-codepoint glyph
    def each_single_cp(&b)
        Dir.entries(@path).each do |p|
            next if ['.', '..'].include?(p)
            sym = @fn2cp.call(p)
            next if sym.length > 1
            b.call("#{@path}/#{p}", sym[0].to_i(16))
        end
    end

    # Write atlas containing all single-codepoint glyphs to stream
    def write_resourcepack_atlas(stream, stride, cp_writer)
        batch = []
        each_single_cp do |path, codepoint|
            batch.push({ :path => path, :code => codepoint })
        end

        make_atlas(batch, stream, stride, @size, cp_writer)
    end
end

# Utility class for collecting codepoints of glyphs which were
# written to an atlas, in order to built metadata.
# It ingests integer codepoints and builds an array of strings,
# each maximally 'stride' codepoints in length
class CpResPackWriter
    def initialize(stride)
        @stride = stride
        @lines = []
        @cur_line = ""
        @cur_line_count = 0
    end

    attr_reader :lines

    def collect(cp)
        @cur_line += [cp].pack('U')
        @cur_line_count += 1

        if @cur_line_count == @stride
            @lines.push @cur_line
            @cur_line = ""
            @cur_line_count = 0
        end
    end

    # Fill up the last line with 0 bytes until it reaches @stride length
    def finalize
        if @cur_line_count > 0
            @cur_line += "\0" * (@stride - @cur_line_count)
            @lines.push @cur_line
        end
    end
end

module ResPack
    def self.write_mcmeta(f, desc)
        object = {
            'pack' => {
                'pack_format' => 5,
                'description' => desc,
            }
        }

        json = JSON.generate object
        f.puts json
    end

    def self.write_font(f, atlas_fn, ascent, chars_ary, dgj_version, dgj_name)
        object = {
            'providers' => [{
                'type' => 'bitmap',
                'file' => atlas_fn,
                'ascent' => ascent,
                'chars' => chars_ary,
                'duyguji_version' => dgj_version,
                'duyguji_name' => dgj_name,
            }]
        }

        json = JSON.generate object
        f.puts json
    end

    def self.write_respack(zipfile, provider, atlas_stride)
        namespace = provider.name
        desc = "#{namespace} font for duyguji"
        pack_png_emote = '😂'.codepoints[0]
        pack_png_src_fn = provider.highres_codepoint_path(pack_png_emote)

        zipfile.get_output_stream('pack.mcmeta') do |stream|
            write_mcmeta(stream, desc)
        end

        zipfile.get_output_stream('pack.png') do |stream|
            File.open(pack_png_src_fn, 'rb') do |file|
                stream.write(file.read)
            end
        end

        font_fn = "assets/minecraft/font/#{namespace}.json"
        atlas_fn_local = "font/duyguji_#{namespace}_atlas.png"
        atlas_fn = "assets/minecraft/textures/#{atlas_fn_local}"

        cp_writer = CpResPackWriter.new(atlas_stride)
        zipfile.get_output_stream(atlas_fn) do |stream|
            provider.write_resourcepack_atlas(stream, atlas_stride, cp_writer)
        end
        cp_writer.finalize

        zipfile.get_output_stream(font_fn) do |stream|
            write_font(stream, atlas_fn_local, 8, cp_writer.lines, 1, namespace)
        end
    end

    def self.build(filename, provider, atlas_stride)
        Zip::File.open(filename, Zip::File::CREATE) do |zipfile|
            write_respack(zipfile, provider, atlas_stride)
        end
        nil
    end
end
