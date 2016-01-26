# Registry

This is the central registry of applications we build. The main use cases:

  - assign unique ports to each application

  - manage port numbers so there is some logic to their assignment to
    minimize human friction long term

  - support docker for development environments

# API

Create the application and allocates a single port for it. Note
that the implementation leaves room adjacent to the allocated port so
that if you need another port in the future it is likely to be
sequential.

    curl -d id=splashpage -d type=api http://registry.api.flow.io/applications

    curl -d id=www -d type=ui http://registry.api.flow.io/applications

    curl -d id=splashpage-postgresql -d type=database http://registry.api.flow.io/applications

or you can use PUT which will upsert the application, as well as
allocate a part for this application type if not already allocated.

    curl -X -d type=api PUT http://registry.api.flow.io/applications/splashpage

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
 