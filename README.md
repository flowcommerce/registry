[![Build Status](https://travis-ci.org/flowcommerce/registry.svg?branch=main)](https://travis-ci.org/flowcommerce/registry)

# Registry

This is the central registry of applications we build. The main use cases:

  - assign unique ports to each application

  - manage port numbers so there is some logic to their assignment to
    minimize human friction long term and help humans understand what
    type of application is running based on port number

  - support docker for development environments

The design of the system includes:

  - services which have names (e.g. postgresql) and default ports
    (e.g. 5432). This enables identifying consistently the internal
    ports an application users.

  - applications which are services w/ a list of assigned ports. These
    ports map the internal port numbers to a globally unique external
    port number.

  - declared dependencies on other applications. This can be used to,
    for example, generate the appropriate sequence in which to start
    the applications so that they their dependencies are started in
    order. Note that we enforce that there can be no cyclical
    dependencies.

# CLI

    See https://github.com/flowcommerce/workstation/blob/main/bin/registry

# API

Create the application and allocates a single port for it. Note
that the implementation leaves room adjacent to the allocated port so
that if you need another port in the future it is likely to be
sequential.

    curl -d id=splashpage -d service=play http://registry.api.flow.io/applications

    curl -d id=www -d service=nodejs http://registry.api.flow.io/applications

    curl -d id=splashpage-postgresql -d service=nodejs http://registry.api.flow.io/applications

or you can use PUT which will upsert the application, as well as
allocate a port for this service if not already allocated.

    curl -X PUT -d service=play http://registry.api.flow.io/applications/splashpage

# Notes:

We also provide consistent external port allocations based on the service name:

  - nodejs: 0
  - play: 1
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
Thu Jul  7 17:59:02 EDT 2022
