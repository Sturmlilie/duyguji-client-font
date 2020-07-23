#!/bin/ruby

# This script converts "shortcodes.json" into the familiar
# symbol/shortcode format used in duyguji.

require 'json'

SCDB = File.open("shortcodes.json", 'r') do |stream|
    JSON.parse stream.read
end

def strip_colons(string)
    string[1..-2]
end

File.open("shortcodes.txt", 'w') do |stream|
    SCDB["emojis"].each do |entry|
        symbol = entry["emoji"]
        shortcode = entry["shortname"]

        next if symbol.nil? or shortcode.nil?
        next if symbol.empty? or shortcode.empty?
        # Filter out any emoji composed of multiple codepoints for now
        # since we can't handle them yet
        sc_ary = symbol.unpack('U*')
        next if sc_ary.length > 1
        # Filter out emoji from plane 0 since we currently don't
        # render them
        next if sc_ary[0] >> 16 == 0

        shortcode = strip_colons(shortcode).gsub('_', '-')
        stream.puts "#{symbol}/#{shortcode}"
    end
end
