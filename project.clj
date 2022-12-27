(defproject ASCIIClojure "0.1"
  :description "ASCII art generator"
  :url "https://github.com/derinsh/ASCIIClojure"
  :license {:name "Eclipse Public License - v 2.0"
            :url "https://www.eclipse.org/legal/epl-v20.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.slf4j/slf4j-api "2.0.6"]
                 [org.slf4j/slf4j-simple "2.0.6"]
                 [com.ibasco.gifreader/gif-reader "1.1.0"]]
  :main main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-binplus "0.6.6"]]}}
  :bin {:name "ascii-clojure"})
