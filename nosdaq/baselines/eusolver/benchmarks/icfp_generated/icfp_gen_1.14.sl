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
(constraint (= (f #xE1E162506618595C) #x0000E1E1E3F16658))
(constraint (= (f #x3CCBAD98715B2962) #x00003CCBBDDBFDDB))
(constraint (= (f #xF1E2533F4A48D922) #x0000F1E2F3FF5B7F))
(constraint (= (f #xB7B214A2251D1460) #x0000B7B2B7B235BF))
(constraint (= (f #x7362C42EB1167BE3) #x00007362F76EF53E))
(constraint (= (f #x6F35077C892ABDB3) #xF9CEFF8B77FD566C))
(constraint (= (f #x54F19F67E95B9370) #xFBBEE69997EE6ECF))
(constraint (= (f #x7CFCD0D13888B0B2) #xFB333FFEEF777FFD))
(constraint (= (f #x907D9846264C96F0) #xFFFA67FBDDBB7F9F))
(constraint (= (f #xE69F6FF60F7053EC) #xF9F69909FF8FFED3))
(constraint (= (f #x000000000000F701) #x0000000000000000))
(constraint (= (f #x0000000000002AE2) #x0000000000000000))
(constraint (= (f #x000000000000965C) #x0000000000000000))
(constraint (= (f #x000000000000FB62) #x0000000000000000))
(constraint (= (f #x000000000000A57D) #x0000000000000000))
(constraint (= (f #x4000000000006962) #x0000400000000000))
(constraint (= (f #x400000000000541F) #x0000400000000000))
(constraint (= (f #x40000000000040E2) #x0000400000000000))
(constraint (= (f #x8000000000006E01) #x0000800000000000))
(constraint (= (f #x8000000000007362) #x0000800000000000))
(constraint (= (f #x00000000001E635B) #x000000000000001E))
(constraint (= (f #x000000000013D65D) #x0000000000000013))
(constraint (= (f #x00000000001DF6BB) #x000000000000001D))
(constraint (= (f #x0000000000141263) #x0000000000000014))
(constraint (= (f #x00000000001AFB05) #x000000000000001A))
(constraint (= (f #x3DA34566E3FF5E27) #x00003DA37DE7E7FF))
(constraint (= (f #x7C6F43911E799C18) #x00007C6F7FFF5FF9))
(constraint (= (f #x84D4E909BE8892F9) #x000084D4EDDDFF89))
(constraint (= (f #x2DF2BD33203D357A) #x00002DF2BDF3BD3F))
(constraint (= (f #xE0220FD374217D27) #x0000E022EFF37FF3))
(constraint (= (f #x000000000000FE6E) #x0000000000000000))
(constraint (= (f #x000000000000E5F2) #x0000000000000000))
(constraint (= (f #x000000000000A68E) #x0000000000000000))
(constraint (= (f #x0000000000008B8D) #x0000000000000000))
(constraint (= (f #x0000000000001A0B) #x0000000000000000))
(constraint (= (f #x4000000000004794) #xFFFFFFFFFFFFFBEF))
(constraint (= (f #x8000000000007F30) #xFFFFFFFFFFFFF8CF))
(constraint (= (f #x800000000000430F) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x4000000000006515) #xFFFFFFFFFFFFFBEE))
(constraint (= (f #xC000000000004CF4) #xFFFFFFFFFFFFFB3B))
(constraint (= (f #x0000000000148611) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x00000000001E784B) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x000000000014244C) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000103ACD) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x000000000019F5EC) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x014907C5E2872368) #xFFFFFFBBBDFFDDDF))
(constraint (= (f #x24D8106FE3C6B2A9) #xFFB7FFF91DFBDDD7))
(constraint (= (f #xDD2B7A8518E20A56) #xF2FDCD7FEF7DFFFB))
(constraint (= (f #xA8983D843AD58129) #xF777FE7FFD7AFFFF))
(constraint (= (f #x49540F59E64FD296) #xFFEBFFAE79BB2FFF))
(constraint (= (f #x0000000000006460) #x0000000000000000))
(constraint (= (f #x0000000000007B1B) #x0000000000000000))
(constraint (= (f #x0000000000005A3B) #x0000000000000000))
(constraint (= (f #x00000000000072DD) #x0000000000000000))
(constraint (= (f #x000000000000591B) #x0000000000000000))
(constraint (= (f #x000000000000E87A) #xFFFFFFFFFFFFF7FD))
(constraint (= (f #x000000000000A046) #xFFFFFFFFFFFFFFFB))
(constraint (= (f #x000000000000E9E6) #xFFFFFFFFFFFFF779))
(constraint (= (f #x0000000000008287) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x00000000000004D9) #xFFFFFFFFFFFFFFB6))
(constraint (= (f #xC0000000000057C6) #x0000C00000000000))
(constraint (= (f #xC0000000000048F9) #x0000C00000000000))
(constraint (= (f #x8000000000006387) #x0000800000000000))
(constraint (= (f #x4000000000006339) #x0000400000000000))
(constraint (= (f #x4000000000004DC6) #x0000400000000000))
(constraint (= (f #x000000000014A35A) #x0000000000000014))
(constraint (= (f #x0000000000110046) #x0000000000000011))
(constraint (= (f #x00000000001B80E7) #x000000000000001B))
(constraint (= (f #x000000000013235A) #x0000000000000013))
(constraint (= (f #x000000000017F327) #x0000000000000017))
(constraint (= (f #x0000000000007E94) #x0000000000000000))
(constraint (= (f #x0000000000005B8B) #x0000000000000000))
(constraint (= (f #x0000000000004192) #x0000000000000000))
(constraint (= (f #x0000000000004E73) #x0000000000000000))
(constraint (= (f #x0000000000006891) #x0000000000000000))
(constraint (= (f #x0000000000002BD7) #xFFFFFFFFFFFFFD6A))
(constraint (= (f #x000000000000CC76) #xFFFFFFFFFFFFF3B9))
(constraint (= (f #x000000000000E02A) #xFFFFFFFFFFFFFFFD))
(constraint (= (f #x000000000000CE49) #xFFFFFFFFFFFFF3BF))
(constraint (= (f #x0000000000003837) #xFFFFFFFFFFFFFFFC))
(constraint (= (f #x4000000000006149) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #xC000000000006D8A) #xFFFFFFFFFFFFFB77))
(constraint (= (f #x4000000000006C29) #xFFFFFFFFFFFFFBFF))
(constraint (= (f #xC000000000004657) #xFFFFFFFFFFFFFBBA))
(constraint (= (f #x8000000000007069) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000117997) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000192A56) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x00000000001A1889) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x0000000000111AC9) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x00000000001EA7F6) #xFFFFFFFFFFFFFFFF))
(constraint (= (f #x00000000000052E6) #xFFFFFFFFFFFFFFD9))
(constraint (= (f #x0000000000004487) #xFFFFFFFFFFFFFBFF))
(constraint (= (f #x0000000000007279) #xFFFFFFFFFFFFFDDE))
(constraint (= (f #x0000000000007BD9) #xFFFFFFFFFFFFFC66))
(constraint (= (f #x0000000000004727) #xFFFFFFFFFFFFFBDD))
(constraint (= (f #x00000000000054E9) #xFFFFFFFFFFFFFBB7))
(constraint (= (f #x0000000000006088) #xFFFFFFFFFFFFFFF7))
(constraint (= (f #x0000000000007737) #xFFFFFFFFFFFFF8CC))
(constraint (= (f #x0000000000007669) #xFFFFFFFFFFFFF99F))
(constraint (= (f #x000000000000670A) #xFFFFFFFFFFFFF9FF))
(check-synth)
