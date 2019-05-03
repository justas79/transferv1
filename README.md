# Transfer money between accounts using Java Play and H2

###Description

JAVA REST API for creating/updating accounts, transferring money between accounts and listing transfers.
Technologies used:
- Java Play framework
- H2 in memory database
- Ebean 

###Usage
Server listens at port 9000. 
Services:
- GET  /api/1.0/accounts/list         
    - Lists existing accounts
- POST /api/1.0/accounts/upsert
    - Creates new or updates existing account. Updatable properties are "name", "active"
    - Body example:
    ````json
    {
    	"name":"justas",
    	"balance":"100",
    	"currencyCode":"EUR",
    	"active":"true"
    }

- POST /api/1.0/accounts/transfer
     - Transfers money between two accounts. If origin account has insufficient funds - waits for some time (in case deposit transaction runs in parallel)
     - Body example: 
     ```json
     {
     	"sourceAccountId":"1",
     	"targetAccountId":"2",
     	"amount":"100"
     }
            
- GET  /api/1.0/accounts/transferList 
     - Lists all transfers

### Launch
To start please clone repository and run ```launch.sh``` script. Ex.:  

```console
git clone https://github.com/justas79/transferv1.git
cd transferv1/
chmod +x launch.sh 
./launch.sh 
```

Alternately1: unzip ```transferv1-1.0-SNAPSHOT.zip``` and launch with command : ```./transferv1-1.0-SNAPSHOT/bin/transferv1 -Dplay.http.secret.key=transferv1secret```

Alternately2: if you have sbt installed, type `sbt run` from the root of the project folder




