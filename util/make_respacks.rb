#!/bin/ruby

# Note: This file is provided from my personal workspace as a reference
# on how to use these helper scripts to build resource packs;
# it refers to paths to preconverted pngs that aren't included in the repository.
# Note that the "high resolution" path of each Provider instance currently only needs
# to contain a glyph for "😂".

load 'respack_util.rb'

DEF_SPLITTER = Proc.new { |fn| fn.chomp('.png').split('-') }
BLOB_SPLITTER = Proc.new { |fn| fn.chomp('.png').sub('emoji_u', '').split('_') }

PROVIDERS = {
    :twe  => Provider.new("twemoji", "providers/twe32", 32, '%x', DEF_SPLITTER, "providers/twe128"),
    :om   => Provider.new("openmoji", "providers/om32", 32, '%X', DEF_SPLITTER, "providers/om128"),
    :emo2 => Provider.new("emojitwo", "providers/emo32", 32, '%x', DEF_SPLITTER, "providers/emo128"),
    :blob => Provider.new("blobmoji", "providers/blob32", 32, 'emoji_u%x', BLOB_SPLITTER, "providers/blob128"),
}

ATLAS_STRIDE = 0x10

PROVIDERS.each do |sym, prov|
    filename = "#{prov.name}_font.zip"
    puts "Building #{filename}..."
    ResPack.build(filename, prov, ATLAS_STRIDE)
end
