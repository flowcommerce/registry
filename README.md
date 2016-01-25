# Registry

Creates the application and allocates a single port for it. Note
that the implementation leaves room adjacent to the allocated port so
that if you need another port in the future it is likely to be
sequential

    curl -d id=splashpage -d type=api http://registry.api.flow.io/applications

    curl -d id=www -d type=ui http://registry.api.flow.io/applications

    curl -d id=splashpage-postgresql -d type=database http://registry.api.flow.io/applications

or you can use PUT which will upsert the application:

    curl -X -d type=api PUT http://registry.api.flow.io/applications/splashpage

Allocate another port for this application

    curl -X POST http://registry.api.flow.io/applications/splashpage/ports

# Notes:

The type of the application is used to provide consistent port allocations:
  - ui: 0
  - api: 1
  - database: 9

# Example allocations:

    splashpage:
      www: 7000
      api: 7001
      psql: 7009

    user
      www: 7010
      api: 7011
      psql: 7019
 