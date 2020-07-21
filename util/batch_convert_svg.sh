#!/bin/bash

if [[ "$#" -le 1 ]]; then
    echo "Usage: ${0} <svg-source-path> <output-path> (<png-size> <parallel-jobs>)"
    exit
fi

SVG_PATH=${1}
OUT_PATH=${2}

# Default size = 32
SIZE=${3:-32}

# Default job count = 4
MAX_JOBS=${4:-4}

echo "SVG_PATH: ${SVG_PATH}"
echo "OUT_PATH: ${OUT_PATH}"

mkdir -p "${OUT_PATH}"

for f in "${SVG_PATH}"/*.svg; do
    # Sprinkling some pseudo-parallel magic
    ((i=i%MAX_JOBS));
    ((i++==0)) && wait;

    OUT_FILE=$(basename -s .svg $f)
    echo "Rendering ${OUT_FILE}.png.."
    rsvg-convert ${f} -w $SIZE -h $SIZE -f png -o "${OUT_PATH}/${OUT_FILE}.png" &
done
