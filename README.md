# Registry

Creates the application and allocates a single port for it. Note
that the implementation leaves room adjacent to the allocated port so
that if you need another port in the future it is likely to be
sequential

    curl -d id=splashpage-api http://registry.api.flow.io/applications

or you can use PUT which will upsert the application:

    curl -X PUT http://registry.api.flow.io/applications/splashpage-api

Allocate another port for this application

    curl -X POST http://registry.api.flow.io/applications/splashpage-api/ports

# Notes:

by default, assign applications whose names end with:
  - www: 0
  - api: 1
  - postgresql: 9

# Example allocations:

    splashpage:
      www: 7000
      api: 7001
      psql: 7009

    user
      www: 7010
      api: 7011
      psql: 7019
 
