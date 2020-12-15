DATA = "{\"initialBalance\": \"${1:-'100.00'}\"}"

curl -X POST -d $DATA -H 'Content-Type: application/json' http://localhost:10000/accounts
