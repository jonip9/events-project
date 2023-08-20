(defproject events-ring-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [com.walmartlabs/lacinia "1.2-alpha-3"]
                 [com.github.seancorfield/honeysql "2.4.1045"]
                 [com.github.seancorfield/next.jdbc "1.3.847"]
                 [hikari-cp "3.0.1"]
                 [org.postgresql/postgresql "42.3.7"]
                 [metosin/jsonista "0.3.7"]
                 [metosin/malli "0.11.0"]
                 [metosin/muuntaja "0.6.8"]
                 [metosin/reitit-core "0.7.0-alpha5"]
                 [metosin/reitit-ring "0.7.0-alpha5"]
                 [metosin/reitit-malli "0.7.0-alpha5"]
                 [metosin/reitit-middleware "0.7.0-alpha5"]]
  :main ^:skip-aot events-ring-server.core
  :ring {:handler events-ring-server.core/app}
  :plugins [[lein-ring "0.12.6"]]
  :repl-options {:init-ns events-ring-server.core})
