(require 'cljs.repl)
(require 'cljs.build.api)
(require 'cljs.repl.node)

(defn read-fn [& args]
  (println args)
  "OUTOU"
  false
  (second args)
  )

(cljs.repl/repl (cljs.repl.node/repl-env)
                :watch "src"
                :output-dir "out"
                :optimizations :none
                :cache-analysis true
                :source-map true
                :read read-fn
                :watch-fn  (fn [] (println "CLJS BUILT!")))


; (cljs.repl/repl (cljs.repl.node/repl-env)
;                 :watch "src"
;                 :output-dir "out"
;                 :optimizations :none
;                 :cache-analysis true
;                 :source-map true
;                 :watch-fn  (fn [] (println "CLJS BUILT!")))

