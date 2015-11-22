# rubber

sync your rethinkdb tables with elasticsearch 

This is an alternative to the java [elasticsearch river plugin](https://github.com/rethinkdb/elasticsearch-river-rethinkdb) used for syncing rethinkdb and elasticsearch.

Rubber differs from the river plugin in the following ways:

- it's not an esoteric elasticsearch plugin
- it's not written in java
- it has configurable logging
- it's faster 
	

### Quick Start

```
docker pull bhurlow/rubber
docker run --link rethink:rethink --elasticsearch:search bhurlow/rubber node index.js prod:customers
```

- args passed into index.js take the form of `db:table`
- you may specify multiple db:table pairs
- log level may be set by the `DEBUG` env var e.g. `DEBUG=info` or `DEBUG=error`
- tables are backfilled automatically 

### Building the Container

```
git clone https://github.com/bhurlow/rubber.git
cd rubber
docker build -t rubber .
```