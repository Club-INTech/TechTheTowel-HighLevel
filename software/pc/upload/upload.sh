#!/bin/bash
LOGIN=intech
ADDRESS=192.168.1.50 #Adresse sur la cisco
#ADDRESS=192.168.43.208 #Adresse sur mon portable
#ADDRESS=157.159.45.36 #Adresse lorsque sur filaire minet
DESTINATION=/home/intech/intech-2015/software/pc

rsync -e ssh --delete-after --exclude-from=exclusion.txt -az ../ "$LOGIN"@"$ADDRESS":"$DESTINATION" ; ssh "$LOGIN"@"$ADDRESS"
