# Clj-Store

A small, superficial read/write key/val configuration utility.

If you were looking to use something like this, I recommend Licenser's [StupidDB](http://github.com/licenser/stupiddb) instead.

## Usage

Wrap commands with `with-store`.

* `(s-keys)`                   - Returns a sequence of the store's keys.
* `(s-vals)`                   - Returns a sequence of the store's vals.
* `(s-get key)`                - Returns the value of the specified key.
* `(s-get-in [ks])`            - Acts as get-in for the store.
* `(s-assoc key val)`          - Associates a key/val to the store.
* `(s-assoc-in [k & ks] val])` - Acts as assoc-in for the store.
* `(s-dissoc key)`             - Removes single key from the store
* `(s-add hmap)`               - Inserts new key/vals or updates the value of already existing ones an arbitrary number of key/vals.
* `(s-update hmap)`            - Updates the value of already existing keys.
* `(s-read file)`              - Reads the map inside the store.
* `(s-clean file)`             - Removes the contents of a file.

### Example

    (with-store "foo.bar"
      (s-add {:a 1 :b 2})
      (s-update {:a 3})
      (s-dissoc :b)
      (s-assoc :c 5))

## Installation

- Leiningen.
- Add `[clj-store "0.1.0"]` to your dependencies in project.clj.

## License

Clj-Store is licensed under the Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
