(defproject ASCIIClojure "0.1"
  :description "ASCII art generator"
  :url "https://github.com/derinsh/ASCIIClojure"
  :license {:name "Eclipse Public License - v 2.0"
            :url "https://www.eclipse.org/legal/epl-v20.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :main ^:skip-aot main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-binplus "0.6.6"]]}}
  :bin {:name "ascii-clojure"})
