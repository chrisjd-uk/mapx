(ns mapx.core-test
  (:require [clojure.test :refer :all]
            [mapx.core :refer :all]))

(def in {:a 1 :b 2 :c 3 :d 4 :e 5})

(deftest test-or
  (is (= {:a 1 :b 2 :c 3 :d 4 :e 5 :f 7}
         (map-xform in :or {:a 123, :f 7}))))

(deftest test-select
  (is (= {:a 1 :c 3}
         (map-xform in :select [:a :c]))))

(deftest test-delete
  (is (= {:a 1 :b 2 :e 5}
         (map-xform in :delete [:c :d]))))

(deftest test-update
  (is (= {:a 1 :b -2 :c 3 :d 40 :e 5}
         (map-xform in :update {:b -, :d (partial * 10)}))))

(deftest test-rename
  (is (= {:x 1 :y 2 :z 3 :d 4 :e 5}
         (map-xform in :rename {:a :x, :b :y, :c :z}))))

(deftest test-all-together
  (is (= {:x 50 :z -3}
         (map-xform in
                    :select [:a :c :e]
                    :delete [:e]
                    :update {:a (partial * 50), :c -}
                    :rename {:a :x, :c :z}))))

(deftest test-projection
  (is (= {:x 1 :z 3}
         (map-xform in
                    :project {:a :x, :c :z}))))

(deftest test-all-together-with-projection
  (is (= {:x 50 :z -3}
         (map-xform in
                    :update {:a (partial * 50), :c -}
                    :project {:a :x, :c :z}))))
