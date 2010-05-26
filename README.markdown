# Clj-Store

A small, superficial read/write key/val configuration utility.

## Usage

Wrap commands with 'with-store'.

* `(s-keys)`                   - "Returns a sequence of the store's keys."
* `(s-vals)`                   - "Returns a sequence of the store's vals."
* `(s-get key)`                - "Returns the value of the specified key."
* `(s-get-in [ks])`            - "Acts as get-in for the store."
* `(s-assoc key val)`          - "Associates a key/val to the store."
* `(s-assoc-in [k & ks] val])` - "Acts as assoc-in for the store."
* `(s-dissoc key)`             - "Removes single key from the store"
* `(s-add hmap)`               - "Inserts new key/vals or updates the value of already existing ones an arbitrary number of key/vals.
* `(s-update hmap)`            - "Updates the value of already existing keys."

    (with-store cabinet
      (& body ...))

## Installation

- Leiningen.

## Idea repository

- A Store type.

## License

FIXME: write
