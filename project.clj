(defproject milkwood-clj "0.1.0"
  :description "Rule driven nonsense generator in Clojure"
  :url "https://github.com/simon-brooke/milkwood-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.4"]]
  :jvm-opts ["-Xss4m"]
  :profiles {:uberjar
             {:main milkwood-clj.core :aot :all}})
