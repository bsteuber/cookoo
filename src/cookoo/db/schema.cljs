(ns cookoo.db.schema
  (:require [cookoo.db.core :refer [attr! class! enum! index-attr!]]
            [cookoo.db.indexers :as idx]
            [cookoo.db.knowledge-base :refer [clear! fact!]]
  	    [cookoo.db.validate :refer [id?]]
            [cookoo.tools.validate :refer [validator]]))  

(def string-v (validator string? "String expected"))
(def number-v (validator number? "Number expected"))
(def fn-v     (validator ifn?    "Function expected"))
(def id-v     (validator id?     "Object-id expected"))

(defn init-schema []
  (clear!)
  (fact! :super :card :set) ;; needed for bootstrapping
  (class! :Value "Value" :Value)
  (class! :HostValue "Host value" :Validated)
  (class! :HostString "Host string" :HostValue
          [] 
          [:validator string-v])
  (class! :HostFn "Host function" :HostValue 
          []
          [:validator fn-v])
  (class! :HostNumber "Host number" :HostValue 
          []
          [:validator number-v])
  (class! :Named "Named" :Value
          [[:name "name" :String]])
  (class! :Object "Object" :Validated
          [:class "class" :Class] 
          [:validator id-v])
  (class! :Validator "Validator" :Object
          [[:message :HostString "error message"]
           [:pred :HostFn "validator predicate"]])
  (class! :Validated "Validated" :Value
          [[:validator "validators" :HostFunction [:card :list]]])
  (class! :Class "Class" [:Named :Object]
          [[:super "superclass" :Class [[:card :set] 
                                        [:default #{:Object}]]]
           [:has-attr :Class "has attribute" {:index [:attr-owner 
                                                      idx/inverse]}]])
  (class! :Attr "Attribute" [:Named :Object] 
          [:attr-class :Class "attribute class"]
          [:default "default value" :Any [:card :optional]]
          [:card :Card "cardinality" [:default :single]]
          [:trigger :HostFn "trigger" {:index [:source-attr
                                               idx/inverse]} ])
  (class! :Trigger "Trigger" :Object
          [[:source-attr :Attr "source attribute"]
           [:target-attr :Attr "target attribute"]
           [:indexer :HostFn "indexer function"]])

  (class! :DbAttr "DB Attribute" :IAttr [:card :default :index])
  (class! :IndexAttr "Index Attribute" :IAtrr)
  (class! :Enum "Enumeration" :Named)

  (enum! :Card "Cardinality"
    [:single "Single value"]
    [:optional "Optional value"]
    [:set "Set of values"]
    [:list "List of values"])

  #_(index-attr! :instance "instance" :Object :class idx/inverse)

  (class! :Expr "Expression")
  (class! :Has-exprs "Has expressions" [] [:exprs])
  (class! :Host-expr "Host Expression" [:Expr] [:host])
  (class! :Comp-expr "Composed Expression" :Expr)
  (class! :Vec  "Vector" [:Comp-expr :Has-exprs])
  (class! :Set  "Set" [:Comp-expr :Has-exprs])
  (class! :Mapping "Mapping" [] [:map-key :map-val])
  (class! :Map "Map" :Comp-expr [:mappings])
  (class! :Call "Call" [:Comp-expr :Has-exprs] [:op])
  (class! :Var "Variable" [:Named :Expr])
  (class! :Local "Local Variable" :Var)
  (class! :Global "Global Variable" :Var)
  (class! :Binding "Binding" [] [:lhs :rhs])
  (class! :Has-bindings "Has bindings" [] [:bindings])
  (class! :Let "Let" [:Expr :Has-bindings :Has-exprs])
  (class! :Toplevel "Toplevel Form")
  (class! :Gets-Args "Gets Arguments" [] [:args])
  (class! :Fn "Function definition" [:Toplevel :Named :Expr :Has-exprs :Gets-args])
  (class! :Mac "Macro definition" [:Toplevel :Named :Has-exprs :Gets-args])
  (class! :Def "Global Variable Definition" :Toplevel [:binding])
  (class! :Ns "Namespace" [:Named] [:toplevel])

  (attr! :host "host value" :Host)
  (attr! :op "operator" :Expr)
  (attr! :exprs "expressions" :Expr :card :List)
  (attr! :args "arguments" :Local :card :List)
  (attr! :map-key "mapping key" :Expr)
  (attr! :map-val "mapping value" :Expr)
  (attr! :mappings "mappings" :Mapping :card :List)
  (attr! :toplevel "toplevel items" :Toplevel :card :Set)
  (attr! :lhs "left-handed side" :Var)
  (attr! :rhs "right-handed side" :Expr)
  (attr! :binding "binding" :Binding)
  (attr! :bindings "bindings" :Binding :card :List)  
)
