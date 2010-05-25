(ns Clj-Store.core
  (:use [clojure.contrib.duck-streams :only [spit slurp*]])
  (:import java.io.File))

;; "s-" is used a prefix to avoid definition conflicts.
;; The paramater "file" may be absolute or relative

(defn s-new [file]
  "Creates a new file with the specified name."
  (.createNewFile (File. file)))

(defn s-read [file]
  "Reads the map inside the file."
  (read-string (slurp* file)))

(defn s-keys [file]
  "Returns a sequence of the keys in the file."
  (-> file s-read keys))

(defn s-vals [file]
  "Returns a sequence of the values in the file."
  (-> file s-read vals))

(defn s-get [file key]
  "Returns the value of a single key."
  (-> file s-read key))

(defn s-get-in [file ks]
  "See get-in."
  (reduce get (s-read file) ks))

(defn s-assoc [file key val]
  "Adds a single key to a file."
  (let [f (ref (s-read file))]
    (dosync (alter f assoc key val)
	    (spit file @f))))

(defn s-assoc-in [file [k & ks] v]
  "See assoc-in."
  (let [key-args (into [k] ks)
	contents (ref (s-read file))]
    (dosync (alter contents merge (assoc-in @contents key-args v)))
    (spit file @contents)))

(defn s-dissoc [file key]
  "Removes a single key from a file."
  (let [f (ref (s-read file))]
    (dosync (alter f dissoc key)
	    (spit file @f))))

(defn s-add [file hmap & [{:keys [create?] :or {create? false}}]]
  "Inserts new key/vals or updates the value of already existing ones
  an arbitrary number of key/vals. If :create? is true, and a file 
  with the name does not already exist in the directory then a new 
  file will be created."
  (if (and (true? create?) (false? (.exists (File. file)))) (spit (File. file) hmap)
      (spit file 
	    (merge 
	     (s-read file)
	     hmap))))

(defn s-update [file hmap]
  "Updates the value of already existing keys."
  (let [contents (ref (s-read file))
	common-keys (ref (filter #(contains? @contents (key %)) hmap))]
    (dosync (alter contents merge @common-keys))
    (spit file @contents)))
