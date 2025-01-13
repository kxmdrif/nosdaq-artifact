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
(constraint (= (f #xAFDC99E51562FE40) #x2BF726794558BF90))
(constraint (= (f #x502B54DA51F272EA) #x140AD536947C9CBA))
(constraint (= (f #x5331E4BDB1E05630) #x14CC792F6C78158C))
(constraint (= (f #x183C63020022B9E8) #x060F18C08008AE7A))
(constraint (= (f #xB6D87962EF4F1C40) #x2DB61E58BBD3C710))
(constraint (= (f #x70F34895B686A16E) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #xC6BAFEFC3A3F9DBC) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x5190932E478FE4C6) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x889C66617AA204BC) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x955AF811D1FE6A66) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x7BC9928F195E0851) #x0000000000000000))
(constraint (= (f #xAF21F16219179891) #x0000000000000000))
(constraint (= (f #xC6FE1AF9BF99BFD3) #x0000000000000000))
(constraint (= (f #x3B617DAC03484AB1) #x0000000000000000))
(constraint (= (f #x5717A940A03403B3) #x0000000000000000))
(constraint (= (f #xAE4536E2044B76FD) #x0000000000000000))
(constraint (= (f #x664EF35233C9C2F5) #x0000000000000000))
(constraint (= (f #x83A91C44746622C7) #x0000000000000000))
(constraint (= (f #xDB5D5D03419F242D) #x0000000000000000))
(constraint (= (f #xFC6B5BC838D9CABD) #x0000000000000000))
(constraint (= (f #xC10CA337F34F9330) #x304328CDFCD3E4CC))
(constraint (= (f #x6C886670A431B83E) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x18E3175B896EB9F3) #x0000000000000000))
(constraint (= (f #x5FF9F5F78EFFA69E) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x472C79A97FC80F8F) #x0000000000000000))
(constraint (= (f #x4186561B61C6EDBA) #x10619586D871BB6E))
(constraint (= (f #xB235C2391E0869BB) #x0000000000000000))
(constraint (= (f #x999009FA87E99173) #x0000000000000000))
(constraint (= (f #xFE7F93400CD09222) #x3F9FE4D003342488))
(constraint (= (f #x640BE3492B2CEEC8) #x1902F8D24ACB3BB2))
(constraint (= (f #x7495109E348C06DC) #xFFFFFFFFFFFFFFFE))
(check-synth)
