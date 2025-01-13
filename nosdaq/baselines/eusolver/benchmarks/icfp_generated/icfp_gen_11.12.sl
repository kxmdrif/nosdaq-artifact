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
(constraint (= (f #xE2582A071A2A4027) #x0000712C15038D14))
(constraint (= (f #x1BBB9CE646E6BBE7) #x00000DDDCE732372))
(constraint (= (f #x8BB9DA4A7C216663) #x000045DCED253E11))
(constraint (= (f #x5CA40DC1D8297E75) #x00002E5206E0EC15))
(constraint (= (f #x330B572D86A88E4B) #x00001985AB96C355))
(constraint (= (f #x41CF48A62C5073FE) #x041CF48A62C50740))
(constraint (= (f #xFB119E35FC3611F2) #x0FB119E35FC3611F))
(constraint (= (f #x7183FFEB04D6FA64) #x07183FFEB04D6FA6))
(constraint (= (f #x8F9D11F1D21F213A) #x08F9D11F1D21F213))
(constraint (= (f #x5A2976F47BD2CF6E) #x05A2976F47BD2CF7))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #x0000000000000001))
(constraint (= (f #x0005555555555556) #x00000002AAAAAAAB))
(constraint (= (f #x5555555555555556) #x00002AAAAAAAAAAB))
(constraint (= (f #x0155555555555556) #x000000AAAAAAAAAB))
(constraint (= (f #x0000000005555556) #x00000000000002AB))
(constraint (= (f #x1555555555555556) #x00000AAAAAAAAAAB))
(check-synth)
