(require 'cljs.build.api)

(cljs.build.api/watch
  "src"
  {:main 'rubber.core
   :output-to "out/main.js"
   :target :nodejs})
