(defproject rubber "0.0.1"
  :description "sync rethinkdb and elasticsearch"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.apa512/rethinkdb  "0.11.0"]
                 [com.taoensso/timbre "4.1.4"]
                 [http-kit  "2.1.18"]
                 [org.clojure/core.async  "0.2.374"]]
  :main rubber.core)
