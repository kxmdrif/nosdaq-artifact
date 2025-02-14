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
(constraint (= (f #xDEED555D1769E1F1) #x00000006622AA8A8))
(constraint (= (f #x2A5D814E2B25CCBD) #x0000000040080051))
(constraint (= (f #x6E6594D4288438A8) #x000000032024A000))
(constraint (= (f #x7EFEA02E9B509754) #x00000003F5010050))
(constraint (= (f #xC3CFF8DF9A059B71) #x000000061E46C4D0))
(constraint (= (f #x0000000000000000) #x0000000000000000))
(constraint (= (f #x028228EA66894583) #x0A08A3A99A25160A))
(constraint (= (f #xBC6603266CE1533B) #xF1980C99B3854CEA))
(constraint (= (f #xA9939FC60818C727) #xA64E7F1820631C9A))
(constraint (= (f #x85A9EFD3D1E3FD83) #x16A7BF4F478FF60A))
(constraint (= (f #xFA762F33217C538E) #xE9D8BCCC85F14E3A))
(constraint (= (f #x2EA84DD48FFBA05C) #x0000000140422424))
(constraint (= (f #xB58385290BCCF7B0) #x000000040C080848))
(constraint (= (f #x2233706753BD32A4) #x0000000111830218))
(constraint (= (f #xCF132628CA0398D2) #x3C4C98A3280E634A))
(constraint (= (f #xC7B3040A2F3A1DDC) #x0000000418000051))
(constraint (= (f #x0805AA43B8223AB6) #x2016A90EE088EADA))
(constraint (= (f #xCA5FD4C843005DE8) #x0000000252A60200))
(constraint (= (f #x326A0450107B1B25) #x0000000110000080))
(constraint (= (f #xBD9E6EE874C17A6B) #xF679BBA1D305E9AA))
(constraint (= (f #x56AD35F5AC945CD4) #x000000002129AD24))
(constraint (= (f #x0000000000000000) #x0000000000000000))
(check-synth)
