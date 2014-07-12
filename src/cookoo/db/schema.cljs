(ns cookoo.db.schema
  (:require [cookoo.db.core :refer [fact! class! enum! validator!]]
            [cookoo.db.indexers :as idx]
            [cookoo.db.knowledge-base :as kb]
  	    [cookoo.db.uid :refer [id?]]
            [cookoo.tools.debug :refer [fail]]
            [cookoo.tools.validate :refer [validator]]))  

(defn init-schema []
  (class! :Value "Value" :Value)
  (class! :Host-val "Host value" :Value :Validated)
  (class! :Host-str "Host string" :Host-value
          [:validator (validator! :str-v "String expected" string?)])
  (class! :Host-fn "Host function" :Host-value
          [:validator (validator! :fn-v "Function expected" ifn?)])
  (class! :Host-num "Host number" :Host-value
          [:validator (validator! :num-v "Number expected" number?)])
  (class! :Object "Object" :Value :Validated
          [[:class "class" :Class]] 
          [:validator (validator! :obj-v "Object expected" id?)])
  (class! :Titled "Titled" :Value
          [[:title "title" :String]])
  (class! :Inherits "Inherits" :Object
          [[:inherit "inherit" :Object]])
  (class! :Class "Class" [:Titled :Inherits] :Metaclass
          [[:has-attr "has attribute" :Class 
            {:index :attr-owner}]
           [:instance "instance" :Object
            {:index :class}]])
  (class! :Metaclass "Metaclass" :Class :Metaclass)  
  (class! :Validated "Validated" :Class :Metaclass
          [[:validator "validators" :HostFn]])            
  (class! :Validator "Validator" :Object
          [[:message "error message" :HostString]
           [:pred "validator predicate" :HostFn]])  
  (class! :Attr "Attribute" [:Titled :Object] 
          [[:attr-owner "attribute owner" :Class]
           [:attr-class "attribute class" :Class]
           [:attr-reader "attribute reader" :HostFn]
           [:index "index" :Index {:index :source-attr} ]])
  (class! :Index "Index" :Object
          [[:source-attr "source attribute" :Attr]
           [:target-attr "target attribute" :Attr]
           [:indexer "indexer function" :HostFn]])
  (class! :Has-default "Has default" :Attr
          [[:default "default value" :Value [:slot-type :optional]]])
  (class! :DbAttr "DB Attribute" :Has-default)
  (class! :IndexAttr "Index Attribute" :Atrr)
  (class! :Enum "Enumeration" :Titled))
