(set-env!
 :dependencies '[[adzerk/boot-cljs "2.1.5" :scope "test"]
                 [adzerk/boot-cljs-repl "0.4.0" :scope "test"]
                 [adzerk/boot-test "1.2.0" :scope "test"]
                 [buddy/buddy-auth "2.2.0"]
                 [buddy/buddy-core "1.6.0"]
                 [buddy/buddy-hashers "1.4.0"]
                 [buddy/buddy-sign "3.1.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [cheshire "5.9.0"]
                 [clojure.java-time "0.3.2"]
                 [com.cognitect/transit-clj "0.8.319"]
                 [com.google.javascript/closure-compiler-unshaded "v20190618" :scope "provided"]
                 [com.walmartlabs/lacinia "0.32.0"]
                 [conman "0.8.4"]
                 [cprop "0.1.15"]
                 [crisptrutski/boot-cljs-test "0.3.4" :scope "test"]
                 [deraen/boot-sass "0.3.1" :scope "test"]
                 [expound "0.8.3"]
                 [funcool/struct "1.4.0"]
                 [lein-kibit "0.1.2" :scope "test"]
                 [luminus-aleph "0.1.6"]
                 [luminus-migrations "0.6.6"]
                 [luminus-transit "0.1.2"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.10.1"]
                 [metosin/muuntaja "0.6.6"]
                 [metosin/reitit "0.3.10"]
                 [metosin/ring-http-response "0.9.1"]
                 [mount "0.1.16"]
                 [nrepl "0.6.0"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.597" :scope "provided"]
                 [org.clojure/core.async "0.4.500"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/google-closure-library "0.0-20190213-2033d5d9" :scope "provided"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.5.0"]
                 [org.webjars.npm/bulma "0.8.0"]
                 [org.webjars.npm/material-icons "0.3.1"]
                 [org.webjars/webjars-locator "0.38"]
                 [org.xerial/sqlite-jdbc "3.28.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.8.0"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.18"]
                 [thheller/shadow-cljs "2.8.69" :scope "provided"]]
 :source-paths #{"src/cljs" "src/cljc" "src/clj"}
 :resource-paths #{"resources"})
(require '[clojure.java.io :as io]
         '[clojure.edn :as edn]
         '[adzerk.boot-test :refer [test]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl]])

(require '[deraen.boot-sass :refer [sass]])


(deftask dev
  "Enables configuration for a development setup."
  []
  (set-env!
   :source-paths #(conj % "env/dev/clj" "src/cljs" "src/cljc" "env/dev/cljs")
   :resource-paths #(conj % "env/dev/resources")
   :dependencies #(concat % '[[prone "1.1.4"]
                              [ring/ring-mock "0.3.0"]
                              [ring/ring-devel "1.6.1"]
                              [pjstadig/humane-test-output "0.8.2"]
                              [binaryage/devtools "0.9.11"]
                              [cider/piggieback "0.4.2"]
                              [com.cemerick/piggieback "0.2.1" :scope "test"]
                              [crisptrutski/boot-cljs-test "0.3.4" :scope "test"]
                              [org.clojure/clojurescript cljs-version :scope "test"]
                              [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                              [pjstadig/humane-test-output "0.10.0"]
                              [powerlaces/boot-figreload "0.1.1-SNAPSHOT" :scope "test"]
                              [prone "2019-07-08"]
                              [ring/ring-devel "1.8.0"]
                              [ring/ring-mock "0.4.0"]
                              [weasel "0.7.0" :scope "test"]]))
  (require 'pjstadig.humane-test-output)
  (let [pja (resolve 'pjstadig.humane-test-output/activate!)]
    (pja))
  (.. System (getProperties) (setProperty "conf" "dev-config.edn"))
  identity)

(deftask testing
  "Enables configuration for testing."
  []
  (dev)
  (set-env! :resource-paths #(conj % "env/test/resources"))
  (merge-env! :source-paths ["src/cljc" "src/cljs" "test/cljs"])
  (.. System (getProperties) (setProperty "conf" "test-config.edn"))
  identity)

(deftask prod
  "Enables configuration for production building."
  []
  (merge-env! :source-paths #{"env/prod/clj" "env/prod/cljs"}
              :resource-paths #{"env/prod/resources"})
  identity)

(deftask start-server
  "Runs the project without building class files.

  This does not pause execution. Combine with a wait task or use the \"run\"
  task."
  []
  (require 'redblackrose.core)
  (let [-main (resolve 'redblackrose.core/-main)]
    (with-pass-thru _
      (apply -main *args*))))

(deftask run
  "Starts the server and causes it to wait."
  []
  (comp
   (apply start-server "--" *args*)
   (wait)))

(require '[clojure.java.io :as io])
(require '[crisptrutski.boot-cljs-test :refer [test-cljs]])
(deftask figwheel
  "Runs figwheel and enables reloading."
  []
  (dev)
  (task-options! repl {:port 7002})
  (require '[powerlaces.boot-figreload :refer [reload]])
  (let [reload (resolve 'powerlaces.boot-figreload/reload)]
    (comp
     (start-server)
     (watch)
     (reload :client-opts {:debug true})
     (cljs-repl)
     (cljs))))

(deftask run-cljs-tests
  "Runs the doo tests for ClojureScript."
  []
  (comp
   (testing)
   (test-cljs :cljs-opts {:output-to "target/test.js", :main "redblackrose.doo-runner", :optimizations :whitespace, :pretty-print true})))

(deftask uberjar
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (prod)
   (aot :namespace #{'redblackrose.core})
   (cljs :optimizations :advanced)
   (uber)
   (jar :file "redblackrose.jar" :main 'redblackrose.core)
   (sift :include #{#"redblackrose.jar"})
   (target)))

