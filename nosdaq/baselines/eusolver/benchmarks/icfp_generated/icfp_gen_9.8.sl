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
(constraint (= (f #xFE8056DBF710AFF3) #xFE8056DBF710AFF3))
(constraint (= (f #x7057DD603D95C2AB) #x7057DD603D95C2AB))
(constraint (= (f #xB5C3DEAF616089A7) #xB5C3DEAF616089A7))
(constraint (= (f #xF3A71CCD156AB65B) #xF3A71CCD156AB65B))
(constraint (= (f #xA0C20D24C041E411) #xA0C20D24C041E411))
(constraint (= (f #x0000000000000003) #x0000000000000003))
(constraint (= (f #x0000000000000005) #x0000000000000005))
(constraint (= (f #x0000000000000007) #x0000000000000007))
(constraint (= (f #x0000000000000001) #x0000000000000001))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #x0001FFFFFFFFFFFE))
(constraint (= (f #x90FAB3190E983390) #x000121F566321D30))
(constraint (= (f #xC63D116374B44D08) #x00018C7A22C6E968))
(constraint (= (f #x6FAD5064BBECFC64) #x0000DF5AA0C977D8))
(constraint (= (f #xBCE625D856A80A16) #x000179CC4BB0AD50))
(constraint (= (f #x1B687936BC43B65A) #x000036D0F26D7886))
(constraint (= (f #x000000000000000E) #x0000000000000000))
(constraint (= (f #x0000000000000004) #x0000000000000000))
(constraint (= (f #x000000000000000C) #x0000000000000000))
(constraint (= (f #x0000000000000006) #x0000000000000000))
(constraint (= (f #x0000000000000002) #x0000000000000000))
(constraint (= (f #xD154CC5089AFDC18) #x0001A2A998A1135E))
(constraint (= (f #x2CC58AEC00A7459E) #x0000598B15D8014E))
(constraint (= (f #x1B5D9811A52DEC44) #x000036BB30234A5A))
(constraint (= (f #x57B0888107F2B2F7) #x57B0888107F2B2F7))
(constraint (= (f #xB5C7925A909C4BC7) #xB5C7925A909C4BC7))
(constraint (= (f #x64F9C9784D5E3210) #x0000C9F392F09ABC))
(constraint (= (f #x5AA6C0C176ABEE29) #x5AA6C0C176ABEE29))
(constraint (= (f #xE2419DF1D3E3EBF4) #x0001C4833BE3A7C6))
(constraint (= (f #x692AA92421CD0426) #x0000D2555248439A))
(constraint (= (f #xCBE949330ED28A57) #xCBE949330ED28A57))
(constraint (= (f #x0000000000000007) #x0000000000000007))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #x0001FFFFFFFFFFFE))
(constraint (= (f #x7FFFFFFFFFFFFFFF) #x7FFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000000002) #x0000000000000000))
(constraint (= (f #x0000000000000004) #x0000000000000000))
(check-synth)
