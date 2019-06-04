# baboonkeeper
Assignment for distributed systems course

# Usage
## Init
- Run `docker-compose up`.
- Start the app.

## Zoo CLI
Connect to `localhost:2181`, `localhost:2182` or `localhost:2183` with zooCli.
- When you create a "/z" node, notepad will be started.
- When you delete it, notepad will be killed.

## App commands
When the app is running it supports some commands:
- `p` prints child tree of "/z" node
- `q` exits the application
