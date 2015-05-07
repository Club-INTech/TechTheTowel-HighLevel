#!/bin/bash
LOGIN=intech
IPFIXE_RASPI="192.168.1.50"
DOSSIER_DESTINATION=/home/intech/intech-2015/software/pc/

rsync -e ssh --delete-after --exclude-from exclusion.txt -az ../ "$LOGIN"@"$IPFIXE_RASPI":"$DOSSIER_DESTINATION"
