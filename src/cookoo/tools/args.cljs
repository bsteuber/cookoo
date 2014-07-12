(ns cookoo.tools.args
  (:require [cookoo.tools.coll :refer [as-seq]]))

(defn combine-parsers [combine-results init parsers]
  (fn [stream]    
    (if (empty? parsers)
        [init stream]
        (let [[p & ps] parsers
              c (combine-parsers combine-results init ps)
              [res-1 stream] (p stream)
              [res-2 stream] (c stream)              
              res (combine-results res-1 res-2)]
          [res stream]))))

(def combine-seq (partial combine-parsers cons nil))

(defn optarg-parser [spec]
  (let [[pred default] (as-seq spec)]    
    (fn [args]
      (let [[x & more] args]
        (if (nil? x)
          [default more]
          (if (pred x)
            [x more]
            [default (seq args)]))))))

(defn parse [stream parser]
  (parser stream))

(defn parse-optargs [args & specs]
  (->> specs
       (map optarg-parser)
       combine-seq
       (parse args)
       (apply concat)))
