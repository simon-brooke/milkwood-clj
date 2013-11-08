(defproject milkwood-clj "0.1.0-SNAPSHOT"
  :description "Reimplementation of the Milkwood rule driven nonsense generator in Clojure"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main milkwood-clj.core
  :jvm-opts ["-Xss4m"]
  :profiles {:uberjar {:aot :all}})
