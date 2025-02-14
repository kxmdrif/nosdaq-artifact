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
(constraint (= (f #x1BEBC0A316A66248) #x1BEBC0A316A6624A))
(constraint (= (f #xDE008456260580AA) #xDE008456260580AC))
(constraint (= (f #x652687CD7CF3058A) #x652687CD7CF3058C))
(constraint (= (f #xC53035E3360D75AE) #xC53035E3360D75B0))
(constraint (= (f #x2A8C32041CE3873E) #x2A8C32041CE38740))
(constraint (= (f #x0000000000000004) #xFFFFFFFFFFFFFFF7))
(constraint (= (f #x0000000000000006) #xFFFFFFFFFFFFFFF3))
(constraint (= (f #x27E3B6185FC280AD) #xB03893CF407AFEA5))
(constraint (= (f #x0CC27F54762BC51F) #xE67B015713A875C1))
(constraint (= (f #x7A9CEA1F91ED837F) #x0AC62BC0DC24F901))
(constraint (= (f #xA503E9D5A5AA6B2F) #xB5F82C54B4AB29A1))
(constraint (= (f #xE9D50B38E540AB25) #x2C55E98E357EA9B5))
(constraint (= (f #x0000000000000005) #xFFFFFFFFFFFFFFF5))
(constraint (= (f #x0000000000000007) #xFFFFFFFFFFFFFFF1))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFD))
(constraint (= (f #x76C89F76AA853AC2) #x76C89F76AA853AC4))
(constraint (= (f #x510EB7757AC470F8) #x510EB7757AC470FA))
(constraint (= (f #x91FC4889409FE1DA) #x91FC4889409FE1DC))
(constraint (= (f #xF8821721907B3815) #x0EFBD1BCDF098FD5))
(constraint (= (f #x10021D4D9F36B25D) #xDFFBC564C1929B45))
(constraint (= (f #x03FC95AFBB02829E) #x03FC95AFBB0282A0))
(constraint (= (f #x1890C76F3ABE5463) #xCEDE71218A835739))
(constraint (= (f #xEC7E713D0807DFB0) #xEC7E713D0807DFB2))
(constraint (= (f #x2B8562931C35D4FE) #x2B8562931C35D500))
(constraint (= (f #xEA2C8095DA4A7DDB) #x2BA6FED44B6B0449))
(constraint (= (f #x0000000000000004) #xFFFFFFFFFFFFFFF7))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFD))
(constraint (= (f #x0000000000000007) #xFFFFFFFFFFFFFFF1))
(check-synth)
