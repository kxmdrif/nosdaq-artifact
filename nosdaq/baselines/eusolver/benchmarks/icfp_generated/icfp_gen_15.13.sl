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
(constraint (= (f #x672BEF74C302EC68) #xC2B2A3071E65855C))
(constraint (= (f #xB936CC74F676EFC8) #x654B416772230268))
(constraint (= (f #x70C7F9C1571C35F4) #xEF970CBA84DBED56))
(constraint (= (f #xE3A4C0A3B4BB5BA6) #xDB3D19531FE1DC38))
(constraint (= (f #x7AB717690AB2DC0E) #xFA38CC3F3433E39C))
(constraint (= (f #xFFFFFFFFFFFFFFFE) #xFFFFFFFFFFFFFFFC))
(constraint (= (f #x0000000000000000) #x0000000000000001))
(constraint (= (f #xE613CDB37FB7B0CD) #xE613CDB37FB7B0CC))
(constraint (= (f #x192F4A7A37891D6B) #x192F4A7A37891D6A))
(constraint (= (f #xAB110FEB059AA901) #xAB110FEB059AA900))
(constraint (= (f #xC3814C4248AB1DD3) #xC3814C4248AB1DD2))
(constraint (= (f #x5A6D7F6847D73927) #x5A6D7F6847D73926))
(constraint (= (f #xDA67C36CA54BE2C5) #xDA67C36CA54BE2C4))
(constraint (= (f #x89CCEE06602B9DD2) #x02A041CC0C52481E))
(constraint (= (f #x02075A6ACC3BD0BA) #x044E5F98C1F0DB62))
(constraint (= (f #x5A634B3C285FC4DE) #xBF8AFF1FD5B47126))
(constraint (= (f #xD84102C6E45B619A) #xAB8A25D5143DAF06))
(constraint (= (f #xBFC14451BCB286C6) #x687AA0294EF35D54))
(constraint (= (f #x601E9B2F180FF829) #x601E9B2F180FF828))
(constraint (= (f #xD7B0F5BCDFA01E1F) #xD7B0F5BCDFA01E1E))
(constraint (= (f #x9CE2B99227A690BB) #x9CE2B99227A690BA))
(constraint (= (f #xE07A8F34636D4DF9) #xE07A8F34636D4DF8))
(constraint (= (f #x0000000000000000) #x0000000000000001))
(constraint (= (f #xFFFFFFFFFFFFFFFE) #xFFFFFFFFFFFFFFFC))
(constraint (= (f #x0000000000000001) #x0000000000000000))
(check-synth)
