(ns rubber.core
  (:require [rethinkdb.query :as r]
            [taoensso.timbre :as timbre :refer (debug info error)]
            [org.httpkit.client :as http]))

(def search-url "http://search:9200")

(defn check-search []
  (let [status (-> @(http/get search-url) :status)]
    (if (= 200 status)
      (info "search connected ✓")
      (error "cannot connect to search @" search-url))))

(defn check-db []
  (let [conn  (r/connect :host  "rethink")]
    (when conn (info "search connected ✓"))))

(defn -main [& args]
  (println "SUP"))


