(set-logic BV)

(define-fun shr1 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000001))
(define-fun shr4 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000004))
(define-fun shr16 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000010))
(define-fun shl1 ((x (BitVec 64))) (BitVec 64) (bvshl x #x0000000000000001))
(define-fun if0 ((x (BitVec 64)) (y (BitVec 64)) (z (BitVec 64))) (BitVec 64) (ite (= x #x0000000000000001) y z))

(synth-fun f ( (x (BitVec 64))) (BitVec 64)
(

(Start (BitVec 64) (#x0000000000000000 #x0000000000000001 x (bvnot Start)
                    (shl1 Start)
 		    (shr1 Start)
		    (shr4 Start)
		    (shr16 Start)
		    (bvand Start Start)
		    (bvor Start Start)
		    (bvxor Start Start)
		    (bvadd Start Start)
		    (if0 Start Start Start)
 ))
)
)
(constraint (= (f #x1537C38D6EF25507) #x75641E394886D57C))
(constraint (= (f #x0466789125F0621B) #x7DCCC3B76D07CEF2))
(constraint (= (f #xAAF1CDF74EC20AA0) #x2A871904589EFAAF))
(constraint (= (f #x75570A6218028E7B) #x45547ACEF3FEB8C2))
(constraint (= (f #xF34581B94DE086BE) #x065D3F23590FBCA0))
(constraint (= (f #x8000000000000001) #x7FFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000000001) #x7FFFFFFFFFFFFFFF))
(constraint (= (f #x5A69DEBA971540D5) #x2CB10A2B4755F957))
(constraint (= (f #xA976C725C016D121) #xB449C6D1FF4976F7))
(constraint (= (f #xE79CEF7668CD6FB8) #xC318844CB994823F))
(constraint (= (f #xD3BD517567D7EBE8) #x62157454C140A0BF))
(constraint (= (f #xA4EDDEFD84D44B9F) #xD8910813D95DA307))
(constraint (= (f #xFFFFFFFFFFFE9CB7) #x0000000000058D23))
(constraint (= (f #xFFFFFFFFFFFE10C0) #x000000000007BCFF))
(constraint (= (f #xFFFFFFFFFFFED7C5) #x000000000004A0EB))
(constraint (= (f #xFFFFFFFFFFFEAFF9) #x000000000005401B))
(constraint (= (f #xFFFFFFFFFFFE9554) #x000000000005AAAF))
(check-synth)
