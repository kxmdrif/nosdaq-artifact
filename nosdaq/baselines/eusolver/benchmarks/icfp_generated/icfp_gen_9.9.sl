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
(constraint (= (f #x589E58DA6313AD4F) #x2C4F2C6D3189D6A7))
(constraint (= (f #x7188E49AE877E501) #x38C4724D743BF280))
(constraint (= (f #x842FCC36935A84D1) #x4217E61B49AD4268))
(constraint (= (f #x66DFAA4E07D481E5) #x336FD52703EA40F2))
(constraint (= (f #xCDF91665C4AEAB19) #x66FC8B32E257558C))
(constraint (= (f #xFFFFFFFFFFFFFFF5) #x7FFFFFFFFFFFFFFA))
(constraint (= (f #xFFFFFFFFFFFFFFF1) #x7FFFFFFFFFFFFFF8))
(constraint (= (f #xFFFFFFFFFFFFFFF3) #x7FFFFFFFFFFFFFF9))
(constraint (= (f #x3C972696A2E4B4C5) #x5AE2B9E1F4570F27))
(constraint (= (f #x3C3D0F16C00F116D) #x5A5B96A220169A23))
(constraint (= (f #x0000F17134B62623) #x00016A29CF113934))
(constraint (= (f #x78B544C8989A6C45) #x350FE72CE4E7A267))
(constraint (= (f #x3C9A99132622D363) #x5AE7E59CB9343D14))
(constraint (= (f #x0000000000000001) #x0000000000000001))
(constraint (= (f #xE5577C8AC2DD2360) #x0E5577C8AC2DD236))
(constraint (= (f #xD83D3070059F2C8C) #x0D83D3070059F2C8))
(constraint (= (f #xE6FB601809C41A30) #x0E6FB601809C41A3))
(constraint (= (f #xB1881D25C0436166) #x0B1881D25C043616))
(constraint (= (f #x1DE58CE90082D5DA) #x01DE58CE90082D5D))
(constraint (= (f #xFFFFFFFFFFFF0002) #x0FFFFFFFFFFFF000))
(constraint (= (f #xFFFFFFFFFFFFFFF0) #x0FFFFFFFFFFFFFFF))
(constraint (= (f #xFFFFFFFFFFFFFFF2) #x0FFFFFFFFFFFFFFF))
(constraint (= (f #x000000000000000E) #x0000000000000000))
(constraint (= (f #x000000000000000A) #x0000000000000000))
(constraint (= (f #xFFFFFFFFFFFFFFF6) #x0FFFFFFFFFFFFFFF))
(constraint (= (f #xA1D55A9DE0624E16) #x0A1D55A9DE0624E1))
(constraint (= (f #x217A47FDAC32B3A0) #x0217A47FDAC32B3A))
(constraint (= (f #x7CA061796D3253EF) #x3E5030BCB69929F7))
(constraint (= (f #xA3ADD909760ED420) #x0A3ADD909760ED42))
(constraint (= (f #xEA909C9D98F0F7CA) #x0EA909C9D98F0F7C))
(constraint (= (f #xE4651C419E8CF391) #x72328E20CF4679C8))
(constraint (= (f #x3098000ED75B115A) #x03098000ED75B115))
(constraint (= (f #xDA4D66A3F3501F9D) #x6D26B351F9A80FCE))
(constraint (= (f #x39ADB5BDF3EF4D48) #x039ADB5BDF3EF4D4))
(constraint (= (f #x6004DD60D1095C0F) #x30026EB06884AE07))
(constraint (= (f #xFFFFFFFFFFFFFFF0) #x0FFFFFFFFFFFFFFF))
(constraint (= (f #x7912D4D45A9B1727) #x359C3F3E87E8A2BA))
(constraint (= (f #x1E444C3C4D879363) #x2D66725A744B5D14))
(constraint (= (f #xA2AC5C504F6F02FB) #x51562E2827B7817D))
(constraint (= (f #x31AF9291588DBAB3) #x18D7C948AC46DD59))
(constraint (= (f #xA63990D48CC62795) #x531CC86A466313CA))
(constraint (= (f #x9E27BF39BF3B8167) #x4F13DF9CDF9DC0B3))
(constraint (= (f #x07907878787A2E2F) #x0B58B4B4B4B74546))
(constraint (= (f #x0789A62E45C96C41) #x0B4E794568AE2261))
(constraint (= (f #x4DEDEB5CED4E02A9) #x26F6F5AE76A70154))
(constraint (= (f #x9046EC36C6450427) #x4823761B63228213))
(constraint (= (f #x3C992E45B26983C9) #x5AE5C5688B9E45AD))
(constraint (= (f #xF45C9888792E45A7) #x6E8AE4CCB5C5687A))
(constraint (= (f #xE62FECD69F2A0F67) #x7317F66B4F9507B3))
(constraint (= (f #x0000000000000001) #x0000000000000001))
(constraint (= (f #xFFFFFFFFFFFF0002) #x0FFFFFFFFFFFF000))
(constraint (= (f #xFFFFFFFFFFFFFFF3) #x7FFFFFFFFFFFFFF9))
(check-synth)
