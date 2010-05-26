(ns Clj-Store.core-test
  (:use [Clj-Store.core] :reload-all)
  (:use [clojure.test]))

(def *test-file* "foo.bar")

(deftest store
  (s-clean *test-file*)
  (with-store "foo.bar"
    (s-add {:a [1 2 3] :b {:c 2} :d 35 :e "83953" :f '([1 2])})
    (s-assoc :g 999)
    (s-update {:a 1})
    (s-dissoc :b))
  (is
   (= {:a 1 :d 35 :e "83953" :f '([1 2]) :g 999}
      (s-read *test-file*))))
