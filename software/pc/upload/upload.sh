#!/bin/bash
LOGIN=intech
DOSSIER_DESTINATION=/home/intech/intech-2015/software/pc/

rsync -e ssh --delete-after --exclude-from exclusion.txt -az ../ "$LOGIN"@"$1":"$DOSSIER_DESTINATION"
