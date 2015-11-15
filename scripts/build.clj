(require 'cljs.build.api)

(cljs.build.api/build
  "src"
  {:main 'rubber.core
   :output-to "out/main.js"
   :target :nodejs})
