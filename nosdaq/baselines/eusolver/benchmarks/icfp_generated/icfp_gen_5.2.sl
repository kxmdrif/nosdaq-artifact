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
(constraint (= (f #x66B6F31B5084F0EA) #x0000000000000000))
(constraint (= (f #x48C8E78809A6207F) #x0000000000000001))
(constraint (= (f #xF7EF1B21E9275D2B) #x0000000000000001))
(constraint (= (f #x593DD92CFF7D0135) #x0000000000000001))
(constraint (= (f #x5ECC720E73FA2938) #x0000000000000000))
(constraint (= (f #xC247C323083ED10A) #x00000C247C323083))
(constraint (= (f #x81C074C8266EDB95) #x0000081C074C8266))
(constraint (= (f #xB52BDC11131BB08B) #x00000B52BDC11131))
(constraint (= (f #xF100C750DDB3C596) #x00000F100C750DDB))
(constraint (= (f #x9C84C19BC5B8720C) #x000009C84C19BC5B))
(constraint (= (f #xFFFFFFFFFFFE20E6) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFE6879) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFE916A) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFECE25) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFE56EA) #x00000FFFFFFFFFFF))
(constraint (= (f #x0000000000000002) #x00000FFFFFFFFFFF))
(constraint (= (f #x0000000000000003) #x00000FFFFFFFFFFF))
(constraint (= (f #x0000000000000000) #x0000000000000000))
(constraint (= (f #xFFFFFFFFFFFE0183) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFECC00) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFE1452) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFEDF83) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFE1A46) #x00000FFFFFFFFFFF))
(constraint (= (f #x296E29C563670C08) #x00000296E29C5636))
(constraint (= (f #x9096A7E3127E9B38) #x0000000000000000))
(constraint (= (f #x7670839C2AE8EB77) #x0000000000000001))
(constraint (= (f #x1AB9E2248573E1EE) #x0000000000000000))
(constraint (= (f #x5E1E722CECD24E91) #x000005E1E722CECD))
(constraint (= (f #x0E362E1E4AE97DED) #x0000000000000001))
(constraint (= (f #xA7E4C4437B4E5E0B) #x00000A7E4C4437B4))
(constraint (= (f #x80EA76A7E097EA87) #x0000080EA76A7E09))
(constraint (= (f #xD06E03C3BA82E5AE) #x0000000000000000))
(constraint (= (f #x868E5AC7D6019609) #x00000868E5AC7D60))
(constraint (= (f #x0000000000000002) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFEFEF1) #x00000FFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFE951C) #x00000FFFFFFFFFFF))
(constraint (= (f #xF3F1717061704161) #x0000000000000001))
(check-synth)
