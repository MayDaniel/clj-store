# Clj-Store

A small, superficial read/write key/val configuration utility.

If you were looking to use something like this, I recommend Licenser's [StupidDB](http://github.com/licenser/stupiddb) instead.

## Usage

Wrap commands with `with-store`.

### Example

    (with-store "foo.bar"
      (assoc :a 1)
      (update-in [:b :c :d] conj 42)
      (dissoc :e)
      (merge {:f 5 :g 6}))

## Installation

- Add `[clj-store "0.2.0"]` to your dependencies.

## License

Clj-Store is licensed under the Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
