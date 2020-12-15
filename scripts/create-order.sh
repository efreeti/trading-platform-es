DATA = "{\"accountId\": \"$1\", \"orderType\": \"${2:-'BUY'}\", \"instrumentId\": \"${3:-'GOOG'}\", \"quantity\": ${4:-'1'}, \"price\": \"${5:-'40.00'}\"}"

curl -X POST -d $DATA -H 'Content-Type: application/json' http://localhost:10001/orders
