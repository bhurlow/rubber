#!/ bin/bash
IP=`dm ip default`
bash -c "echo $IP search >> /etc/hosts"
bash -c "echo $IP rethink >> /etc/hosts"
# sed -i "$IP search" /etc/hosts
# sed -i "$IP rethink" /etc/hosts




