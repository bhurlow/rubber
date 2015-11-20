(defproject rubber "0.0.1"
  :description "sync rethinkdb and elasticsearch"
  :main rubber.boot
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [com.taoensso/timbre "4.1.4"]
                 [jamesmacaulay/cljs-promises "0.1.0"]
                 [com.stuartsierra/component "0.3.0"]
                 [camel-snake-kebab "0.3.2"]
                 [org.clojure/core.async  "0.2.374"]]
  :source-paths ["src"])
