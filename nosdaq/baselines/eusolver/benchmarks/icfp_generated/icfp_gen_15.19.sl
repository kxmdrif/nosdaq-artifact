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
(constraint (= (f #x4C10D178FC48C230) #x0000000000000000))
(constraint (= (f #x51BF12A201C15D5E) #x0000000000000000))
(constraint (= (f #x02A8148A5609965E) #x0000000000000000))
(constraint (= (f #x3768A49E00C44BB4) #x0000000000000000))
(constraint (= (f #x234E7D216DDD24EA) #x0000000000000000))
(constraint (= (f #x163080D0782A157A) #x163081B3702712F8))
(constraint (= (f #xAB47541D24BB31E4) #xAB475EA951FAE3AF))
(constraint (= (f #x70F73E44958F039A) #x70F7394BE66B4AC2))
(constraint (= (f #xE262E78779BE9CB6) #xE262E9A157C6EB2D))
(constraint (= (f #x3A72397C3D221370) #x3A723ADB1EB5D0A2))
(constraint (= (f #x272B33205163726D) #x0000000000000001))
(constraint (= (f #xFD5020996966B1B3) #x0000000000000001))
(constraint (= (f #x65F14929FF3AD8A5) #x0000000000000001))
(constraint (= (f #x96308088B206D863) #x0000000000000001))
(constraint (= (f #x745462D30246E449) #x0000000000000001))
(constraint (= (f #xBEC41E063B25FA75) #xBEC41E063B25FA75))
(constraint (= (f #x827D17FDD178DCED) #x827D17FDD178DCED))
(constraint (= (f #xD882179D5D952BEF) #xD882179D5D952BEF))
(constraint (= (f #x5A17B7C6F80DAD83) #x5A17B7C6F80DAD83))
(constraint (= (f #x7A710CD970F0844B) #x7A710CD970F0844B))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x1FE928B309391AE9) #x1FE928B309391AE9))
(constraint (= (f #x33D6A7073BB1C613) #x33D6A7073BB1C613))
(constraint (= (f #xFA6011BDC6E314EF) #x0000000000000001))
(constraint (= (f #xFA1678417CA44292) #x0000000000000000))
(constraint (= (f #xC152A3CE498678DE) #xC152AFDB63BA9C46))
(constraint (= (f #x487278BEA9AF1DC9) #x0000000000000001))
(constraint (= (f #x8EFB482F8C57DD69) #x0000000000000001))
(constraint (= (f #x0B4D7DAC0BF934D7) #x0B4D7DAC0BF934D7))
(constraint (= (f #x57EAFB6CFCACC0F8) #x0000000000000000))
(constraint (= (f #x7C04C498A23FD428) #x7C04C358EE765E0B))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x7C3F3EBD89B0E84F) #x7C3F3EBD89B0E84F))
(constraint (= (f #xC95AF1F95AFDAD83) #xC95AF1F95AFDAD83))
(constraint (= (f #x5B0B3294049564D0) #x0000000000000000))
(constraint (= (f #x7FFFFFFFFFFFFFFF) #x0000000000000001))
(constraint (= (f #xFFFFFFFFFFFFFFFE) #xFFFFF00000000001))
(check-synth)
