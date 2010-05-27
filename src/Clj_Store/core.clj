(ns Clj-Store.core
  (:use [clojure.contrib.duck-streams :only [spit slurp*]])
  (:import java.io.File))

;; "s-" is used a prefix to avoid definition conflicts.
;; The paramater "file" may be absolute or relative.

(declare *store*)

(defn s-new
  "Creates a new store or file."
  ([] (s-new *store*))
  ([file]
  (if (false? (.exists (File. file)))
    (-> file File. .createNewFile))))

(defmacro with-store
  "Creates - if one does not already exist - a new file, and evaluates
  the body."
  [file & body]
  `(binding [*store* ~file]
  (s-new)
     ~@body))

(defn s-read
  "Reads the map inside the store or a file."
  ([] (s-read *store*))
  ([file]
  (let [f (-> file slurp*)]
    (if (empty? f) {} (read-string f)))))

(defn s-clean
  "Removes the contents of a store or a file."
  ([] (s-clean *store*))
  ([file]
  (spit file "")))

(defn s-keys
  "Returns a sequence of the keys in the store."
  []	
  (-> *store* s-read keys))

(defn s-vals
  "Returns a sequence of the values in the store."
  []
  (-> *store* s-read vals))

(defn s-get
  "Returns the value of a single key."
  [key]
  (-> *store* s-read key))

(defn s-get-in
  "See get-in."
  [ks]
  (reduce get (s-read *store*) ks))

(defn s-assoc
  "Adds a single key to the store."
  [key val]
  (let [f (ref (s-read *store*))]
    (dosync (alter f assoc key val)
	    (spit *store* @f))))

(defn s-assoc-in
  "See assoc-in."
  [[k & ks] v]
  (let [key-args (into [k] ks)
	contents (ref (s-read *store*))]
    (dosync (alter contents merge (assoc-in @contents key-args v)))
    (spit *store* @contents)))

(defn s-dissoc
  "Removes a single key from the store."
  [key]
  (let [f (ref (s-read *store*))]
    (dosync (alter f dissoc key)
	    (spit *store* @f))))

(defn s-add
  "Inserts new key/vals or updates the value of already existing ones
  an arbitrary number of key/vals."
  [hmap]  
  (spit *store* (merge (s-read *store*) hmap)))

(defn s-update
  "Updates the value of keys already existing within the store."
  [hmap]
  (let [contents (ref (s-read *store*))
	common-keys (ref (filter #(contains? @contents (key %)) hmap))]
    (dosync (alter contents merge @common-keys))
    (spit *store* @contents)))
