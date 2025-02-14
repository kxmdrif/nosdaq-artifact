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
(constraint (= (f #x7B445878F90EE82C) #x7B445878F90EE82D))
(constraint (= (f #xE5176E0D332EBD2E) #xE5176E0D332EBD2F))
(constraint (= (f #xA8F1F51ECB240D3A) #xA8F1F51ECB240D3B))
(constraint (= (f #x822A504EC76BCA22) #x822A504EC76BCA23))
(constraint (= (f #x75E511FEA382185C) #x75E511FEA382185D))
(constraint (= (f #x30F65B148AF8C0D3) #x30F65B148AF8C0D4))
(constraint (= (f #x57EF3BDEA2B198E3) #x57EF3BDEA2B198E4))
(constraint (= (f #x1129BFBCBED5F793) #x1129BFBCBED5F794))
(constraint (= (f #x9421122FF5786073) #x9421122FF5786074))
(constraint (= (f #xA9B337BE17BE232B) #xA9B337BE17BE232C))
(constraint (= (f #x0000000000000000) #x0000000000000001))
(constraint (= (f #xA1C06867479C4B54) #xA1E1E86F67DFDF5F))
(constraint (= (f #x64FF87B6E6D2DC60) #x64FFFFB7F6F6DEFC))
(constraint (= (f #x9DD195652971FF78) #x9DDDD5F56D79FFFF))
(constraint (= (f #xED8315444B57F05E) #xEDEF97554F5FF7FE))
(constraint (= (f #x2A8003A3375E688A) #x2AAA83A3B77F7EEA))
(constraint (= (f #x06C21C2F2C308489) #x06C21C2F2C30848B))
(constraint (= (f #x8AAA4276DF904C9D) #x8AAA4276DF904C9F))
(constraint (= (f #xA40931E0F19C5799) #xA40931E0F19C579B))
(constraint (= (f #x7F9D752908F4341D) #x7F9D752908F4341F))
(constraint (= (f #xF414D574A03C6295) #xF414D574A03C6297))
(constraint (= (f #x0A98A67953E7C6EB) #x0A9ABEFF7BF7E7EF))
(constraint (= (f #x2C978BD6F76D9DEB) #x2CBF9FDFF7FFFDFF))
(constraint (= (f #x92FF74C131EB50F7) #x92FFFFF5F1FBFBF7))
(constraint (= (f #x0272A135C76C1127) #x0272F3B5F7EF7D37))
(constraint (= (f #x925D1517004370BB) #x92DF5D17174373FB))
(constraint (= (f #xF6A268A682EC4C35) #xF6F6EAEEA6EEEC7D))
(constraint (= (f #xF93E9C3389C1B809) #xF9FFBEBFBBC9F9B9))
(constraint (= (f #xAD6312FD1D6DF735) #xADEF73FFFD7DFFF7))
(constraint (= (f #x2C7166B3EE6A452D) #x2C7D77F7FFEE6F6D))
(constraint (= (f #x063F39093AADF20D) #x063F3F393BBFFFFF))
(constraint (= (f #x2A10A75C9C3F0F41) #x2A10A75C9C3F0F43))
(constraint (= (f #x8961B3D8B2ED6762) #x8961B3D8B2ED6763))
(constraint (= (f #x189ABBF03AAE9E62) #x189ABBF03AAE9E63))
(constraint (= (f #xD549BF9C6F4730D4) #xD549BF9C6F4730D5))
(constraint (= (f #x87318FF3BC27060B) #x87B7BFFFFFBF270F))
(constraint (= (f #x54692700ACED5254) #x54692700ACED5255))
(constraint (= (f #x41844837151ABF10) #x41C5CC7F371FBFBF))
(constraint (= (f #x26E52A5D061C26BE) #x26E7EF7F5F1E3EBE))
(constraint (= (f #x16A9FC935BE33659) #x16BFFDFFDBFBF77F))
(constraint (= (f #xA2A99C8B61EA213E) #xA2A99C8B61EA213F))
(constraint (= (f #x83BED80EE07F02D3) #x83BED80EE07F02D4))
(constraint (= (f #x19B3B44D1BA46E3E) #x19B3B44D1BA46E3F))
(constraint (= (f #x560AB339CBCCFA95) #x565EBBBBFBCFFEFF))
(constraint (= (f #xFCC2CE28A6D6DA21) #xFCC2CE28A6D6DA23))
(constraint (= (f #x2A1125AE8DE0A42F) #x2A3B35AFAFEDE4AF))
(constraint (= (f #x10D7642937017A46) #x10D7642937017A47))
(constraint (= (f #x1DDF3B09C19129F9) #x1DDF3B09C19129FB))
(constraint (= (f #x91D345BA6802F996) #x91D345BA6802F997))
(constraint (= (f #x97DE6C449535B6F6) #x97DFFE6CD5B5B7F6))
(constraint (= (f #xB9B6ADD389BA6A07) #xB9B6ADD389BA6A08))
(constraint (= (f #x8A7B2ADF2FA51DC8) #x8A7B2ADF2FA51DC9))
(constraint (= (f #x07F63501024DBCB0) #x07F63501024DBCB1))
(constraint (= (f #x8580871826E569D7) #x8585879F3EE7EDFF))
(constraint (= (f #x75C06C8212DC8893) #x75C06C8212DC8894))
(constraint (= (f #xB97E91AA6683409B) #xB9FFFFBBEEE7C3DB))
(check-synth)
