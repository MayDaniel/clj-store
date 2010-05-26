(ns Clj-Store.core
  (:use [clojure.contrib.duck-streams :only [spit slurp*]])
  (:import java.io.File))

;; "s-" is used a prefix to avoid definition conflicts.
;; The paramater "file" may be absolute or relative.

(defn s-new
  "Creates a new file with the specified name."
  [file]
  (.createNewFile (File. file)))

(defn s-read
  "Reads the map inside the file."
  [file]
  (-> file slurp* read-string))

(defn s-keys
  "Returns a sequence of the keys in the file."
  [file]
  (-> file s-read keys))

(defn s-vals
  "Returns a sequence of the values in the file."
  [file]
  (-> file s-read vals))

(defn s-get
  "Returns the value of a single key."
  [file key]
  (-> file s-read key))

(defn s-get-in
  "See get-in."
  [file ks]
  (reduce get (s-read file) ks))

(defn s-assoc
  "Adds a single key to a file."
  [file key val]
  (let [f (ref (s-read file))]
    (dosync (alter f assoc key val)
	    (spit file @f))))

(defn s-assoc-in
  "See assoc-in."
  [file [k & ks] v]
  (let [key-args (into [k] ks)
	contents (ref (s-read file))]
    (dosync (alter contents merge (assoc-in @contents key-args v)))
    (spit file @contents)))

(defn s-dissoc
  "Removes a single key from a file."
  [file key]
  (let [f (ref (s-read file))]
    (dosync (alter f dissoc key)
	    (spit file @f))))

(defn s-add
  "Inserts new key/vals or updates the value of already existing ones
  an arbitrary number of key/vals. If :create? is true, and a file 
  with the name does not already exist in the directory then a new 
  file will be created."
  [file hmap & [{:keys [create?] :or {create? false}}]]
  (if (and (true? create?) (false? (.exists (File. file)))) (spit (File. file) hmap)
      (spit file 
	    (merge 
	     (s-read file)
	     hmap))))

(defn s-update
  "Updates the value of already existing keys."
  [file hmap]
  (let [contents (ref (s-read file))
	common-keys (ref (filter #(contains? @contents (key %)) hmap))]
    (dosync (alter contents merge @common-keys))
    (spit file @contents)))
