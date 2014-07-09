(ns cookoo.db.schema
  (:require [cookoo.db.core :refer [attr! class! enum! index-attr!]
            [cookoo.db.indexers :as idx]
  	    [cookoo.db.validate :refer [id? validate-transaction]])  

(defn init-schema []
  (class! :Any "Anything" :Any)
  (class! :Prim "Primitive" :Any)
  (class! :String "String" :Prim [] :validator [string? "String expected"])
  (class! :Number "Number" :Prim [] :validator [number? "Number expected"])
  (class! :Function "Function" :Any [] :validator [ifn? "Function expected"])
  (class! :Object "Object" :Any [:class] :validator [id? "Object expected"])
  (class! :Named "Named" [] [:name])
  (class! :Class "Class" :Named [:super :has-attr :validator])
  (class! :Attr "Attribute" :Named [:attr-class :validator])
  (class! :DbAttr "DB Attribute" :IAttr [:card :default :index])
  (class! :IndexAttr "Index Attribute" :IAtrr)
  (class! :Enum "Enumeration" :Named)

  (enum! :Card "Cardinality"
    [:single "Single value"]
    [:optional "Optional value"]
    [:set "Set of values"]
    [:list "List of values"])

  (attr! :name "name" :String :default "")
  (attr! :class "class" :Class :default :Object)
  (attr! :super "superclass" :Class :card :list :default :Object)
  (attr! :has-attr "has attribute" :Attr :card :set)
  (attr! :attr-class "attribute class" :Class)
  (attr! :card "cardinality" :Card :default :single)
  (attr! :default "default value" :Any :card :optional)
  (attr! :validator "validator" :Function :card :set)
  (attr! :index "index" :Function :card :set)

  (index-attr! :instance "instance" :Object :class idx/inverse)

  (class! :Expr "Expression")
  (class! :Has-exprs "Has expressions" [] [:exprs])
  (class! :Prim-expr "Primitive Expression" [:Expr] [:prim])
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
  (class! :Let "Let" [:Expr :Has-bindings :Has-exprs] 
  (class! :Toplevel "Toplevel Form")
  (class! :Gets-Args "Gets Arguments" [] [:args])
  (class! :Fn "Function definition" [:Toplevel :Named :Expr :Has-exprs :Gets-args])
  (class! :Mac "Macro definition" [:Toplevel :Named :Has-exprs :Gets-args])
  (class! :Def "Global Variable Definition" :Toplevel [:binding])
  (class! :Ns "Namespace" [:Named] [:toplevel])

  (attr! :prim "primitive" :Prim)
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