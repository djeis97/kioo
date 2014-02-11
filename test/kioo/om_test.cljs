(ns kioo.om-test
  (:require [cemerick.cljs.test :as t]
            [kioo.om :refer [content set-attr append prepend
                             remove-attr before after do->
                             set-style remove-style add-class
                             remove-class wrap unwrap set-class
                             html html-content]]
            [kioo.test :refer [render-dom]]
            [goog.dom :as gdom])
  (:require-macros [kioo.om :refer [component snippet template
                                    defsnippet deftemplate]]
                   [cemerick.cljs.test :refer [are is deftest testing]]))

;; all text get surrounded by spans in om
;; its a bit ugly but it is the expected
;; behavior

(deftest render-test
  (testing "basic render test"
    (let [comp (component "simple-div.html" {}) ]
      (is (= "<div id=\"tmp\"><span>test</span></div>"
             (render-dom comp)))))
  (testing "content replace"
    (let [comp (component "simple-div.html" 
                          {[:div] (content "success")})]
      (is (= "<div id=\"tmp\"><span>success</span></div>"
             (render-dom comp)))))
  (testing "first-of-type naked symbol"
    (let [comp (component "list.html" [:ul [:li first-of-type]] {})]
      (is (= "<li><span>1</span></li>" (render-dom comp)))))
  (testing "attr= content replace"
    (let [comp (component "simple-attr-div.html"
                          {[(attr= :data-id "tmp")]
                           (content "success")})]
      (is (= "<div data-id=\"tmp\"><span>success</span></div>"
             (render-dom comp)))))
  (testing "attr? content replace"
    (let [comp (component "simple-attr-div.html"
                          {[(attr? :data-id)]
                           (content "success")})]
      (is (= "<div data-id=\"tmp\"><span>success</span></div>"
             (render-dom comp)))))
  (testing "append test"
    (let [comp (component "simple-div.html"
                          {[:div] (append "success")})]
      ;;note that ract wraps text nodes in span tags
      ;;this is expected to be corrected soon in react but
      ;;for now this is correct
      (is (= "<div id=\"tmp\"><span>test</span><span>success</span></div>"
             (render-dom comp)))))
  (testing "prepend test"
    (let [comp (component "simple-div.html"
                          {[:div] (prepend "success")})]
      (is (= "<div id=\"tmp\"><span>success</span><span>test</span></div>"
             (render-dom comp)))))
  (testing "set-attr test"
    (let [comp (component "simple-div.html"
                          {[:div] (set-attr :id "success")})]
      (is (= "<div id=\"success\"><span>test</span></div>"
             (render-dom comp)))))
  (testing "remove-attr test"
    (let [comp (component "simple-div.html"
                          {[:div] (remove-attr :id)})]
      (is (= "<div><span>test</span></div>"
             (render-dom comp)))))
  (testing "before test"
    (let [comp (component "simple-div.html"
                          {[:div] (before "success")})]
      (is (= "<span><span>success</span><div id=\"tmp\"><span>test</span></div></span>"
             (render-dom comp)))))
  (testing "after test"
    (let [comp (component "simple-div.html"
                          {[:div] (after "success")})]
      (is (= "<span><div id=\"tmp\"><span>test</span></div><span>success</span></span>"
             (render-dom comp)))))
  (testing "add-class test"
    (let [comp (component "class-span.html" [:span]
                          {[:#s] (add-class "suc")})]
      (is (= "<span class=\"cl cls suc\" id=\"s\"><span>testing</span></span>"
             (render-dom comp)))))
  (testing "remove-class test"
    (let [comp (component "class-span.html" [:span]
                          {[:#s] (remove-class "cl")})]
      (is (= "<span class=\" cls\" id=\"s\"><span>testing</span></span>"
             (render-dom comp)))))
  (testing "set-class test"
    (let [comp (component "class-span.html" [:span]
                          {[:#s] (set-class "cl")})]
      (is (= "<span class=\" cl\" id=\"s\"><span>testing</span></span>"
             (render-dom comp)))))
  (testing "set-style test"
    (let [comp (component "style-span.html" [:span]
                          {[:#s] (set-style :display "none")})]
      (is (= "<span style=\"color:red;display:none;\" id=\"s\"><span>testing</span></span>"
             (render-dom comp)))))
  (testing "remove-style test"
    (let [comp (component "style-span.html" [:span]
                          {[:#s] (remove-style :color)})]
      (is (= "<span id=\"s\"><span>testing</span></span>"
             (render-dom comp)))))
  (testing "do-> test"
    (let [comp (component "style-span.html" [:span]
                          {[:#s] (do->
                                  (remove-attr :id)
                                  (remove-style :color))})]
      (is (= "<span><span>testing</span></span>"
             (render-dom comp)))))
  (testing "wrap test"
    (let [comp (component "wrap-test.html" [:span]
                          {[:#s] (wrap :div {:id "test"})})]
      (is (= "<div id=\"test\"><span id=\"s\"><span>testing</span></span></div>"
             (render-dom comp)))))
  (testing "unwrap test"
    (let [comp (component "wrap-test.html" [:div]
                          {[:div] unwrap})]
      (is (= "<span id=\"s\"><span>testing</span></span>"
             (render-dom comp)))))
  (testing "html test"
    (let [comp (component "simple-div.html"
                          {[:div] (content (html [:h1 {:class "t"}
                                                  [:span "t1"]]))})]
      (is (= "<div id=\"tmp\"><h1 class=\"t\"><span>t1</span></h1></div>"
             (render-dom comp)))))
  (testing "html-content test"
    (let [comp (component "simple-div.html"
                          {[:div] (html-content "<h1>t1</h1><em><span>t2</span></em>")})]
      (is (= "<div id=\"tmp\"><h1>t1</h1><em><span>t2</span></em></div>"
             (render-dom comp))))))




(defsnippet snip1 "wrap-test.html" [:span] [] {})
(deftemplate tmp1 "simple-div.html" [] {})
(defsnippet snip2 "wrap-test.html" [:span] [val]
  {[:span] (content val)})
(deftemplate tmp2 "simple-div.html" [val]
  {[:div] (content val)})

(deftest snippet_template_test
  (testing "basic setup for snippet"
    (let [comp (snippet "wrap-test.html" [:span] [] {})]
      (is (= "<span id=\"s\"><span>testing</span></span>"
             (render-dom (comp))))))
  (testing "basic setup for template"
    (let [comp (template "simple-div.html" [] {})]
      (is (= "<div id=\"tmp\"><span>test</span></div>"
             (render-dom (comp))))))
  (testing "simple tranform for snippet"
    (let [comp (snippet "wrap-test.html" [:span] [val]
                        {[:span] (content val)})]
      (is (= "<span id=\"s\"><span>test</span></span>"
             (render-dom (comp "test"))))))
  (testing "simple transform for template"
    (let [comp (template "simple-div.html" [val]
                         {[:div] (content val)})]
      (is (= "<div id=\"tmp\"><span>success</span></div>"
             (render-dom (comp "success"))))))
  (testing "basic setup for defsnippet"
    (is (= "<span id=\"s\"><span>testing</span></span>"
           (render-dom (snip1)))))
  (testing "basic setup for deftemplate"
    (is (= "<div id=\"tmp\"><span>test</span></div>"
           (render-dom (tmp1)))))
  (testing "simple transform for defsnippet"
    (is (= "<span id=\"s\"><span>test</span></span>"
           (render-dom (snip2 "test")))))
  (testing "simple transform for deftemplate"
    (is (= "<div id=\"tmp\"><span>success</span></div>"
           (render-dom (tmp2 "success"))))))