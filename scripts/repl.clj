(require 'cljs.repl)
(require 'cljs.build.api)
(require 'cljs.repl.node)

(cljs.repl/repl (cljs.repl.node/repl-env)
                :watch "src"
                :output-dir "out"
                :optimizations :none
                :cache-analysis true
                :source-map true
                :watch-fn  (fn [] (println "CLJS BUILT!")))

