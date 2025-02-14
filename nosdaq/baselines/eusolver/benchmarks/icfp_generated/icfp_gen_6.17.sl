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
(constraint (= (f #xEFADC1744CA71557) #x0DF5B82E8994E2AA))
(constraint (= (f #xFE0CD6951E04B0F7) #x0FC19AD2A3C0961E))
(constraint (= (f #xCC6EE4D66129E0CB) #x098DDC9ACC253C19))
(constraint (= (f #x247902C42817997F) #x048F20588502F32F))
(constraint (= (f #x4D52B14BB528134B) #x09AA562976A50269))
(constraint (= (f #xFFFFFFFFFFFE0C87) #x0FFFFFFFFFFFC190))
(constraint (= (f #xFFFFFFFFFFFEEC3B) #x0FFFFFFFFFFFDD87))
(constraint (= (f #xFFFFFFFFFFFE263B) #x0FFFFFFFFFFFC4C7))
(constraint (= (f #xFFFFFFFFFFFECD93) #x0FFFFFFFFFFFD9B2))
(constraint (= (f #xFFFFFFFFFFFEDA2B) #x0FFFFFFFFFFFDB45))
(constraint (= (f #x19BE9C4419DAD5EC) #x19BE9C4419DAD5ED))
(constraint (= (f #x7AB2EC9F6DD805D4) #x7AB2EC9F6DD805D5))
(constraint (= (f #x2781AEB05B1C6C38) #x2781AEB05B1C6C39))
(constraint (= (f #xFBC5691AEEAAC234) #xFBC5691AEEAAC235))
(constraint (= (f #xAF2AC65190EF705C) #xAF2AC65190EF705D))
(constraint (= (f #xB2E067F07853FCE2) #x065C0CFE0F0A7F9C))
(constraint (= (f #x6FA441953F4ACFD6) #x0DF48832A7E959FA))
(constraint (= (f #x6DD95E79C01EF90A) #x0DBB2BCF3803DF21))
(constraint (= (f #x0EE4F1A6FCF689CE) #x01DC9E34DF9ED139))
(constraint (= (f #x89BD953AA51E5E96) #x0137B2A754A3CBD2))
(constraint (= (f #xFFFF000000000002) #x0000000000000000))
(constraint (= (f #xFFFFFFFFFFFE4CC0) #x000000000001B33F))
(constraint (= (f #xFFFFFFFFFFFE668C) #x0000000000019973))
(constraint (= (f #xFFFFFFFFFFFEC314) #x0000000000013CEB))
(constraint (= (f #xFFFFFFFFFFFE62E4) #x0000000000019D1B))
(constraint (= (f #xFFFFFFFFFFFEC694) #x000000000001396B))
(constraint (= (f #xFFFFFFFFFFFE535E) #x0FFFFFFFFFFFCA6B))
(constraint (= (f #xFFFFFFFFFFFE8E1E) #x0FFFFFFFFFFFD1C3))
(constraint (= (f #xFFFFFFFFFFFE8F76) #x0FFFFFFFFFFFD1EE))
(constraint (= (f #xFFFFFFFFFFFE0F5A) #x0FFFFFFFFFFFC1EB))
(constraint (= (f #xFFFFFFFFFFFE7E36) #x0FFFFFFFFFFFCFC6))
(constraint (= (f #x0000000000000000) #x0000000000000000))
(constraint (= (f #x99A0CDBAB5AF3A41) #x99A0CDBAB5AF3A40))
(constraint (= (f #x943F7FE257027249) #x943F7FE257027248))
(constraint (= (f #x4036CA23F8465579) #x4036CA23F8465578))
(constraint (= (f #x9DA9B3422BE61AED) #x9DA9B3422BE61AEC))
(constraint (= (f #x34F631417F2A30FD) #x34F631417F2A30FC))
(constraint (= (f #xFFFFFFFFFFFEF8A1) #x000000000001075E))
(constraint (= (f #xFFFFFFFFFFFEFA11) #x00000000000105EE))
(constraint (= (f #xFFFFFFFFFFFECE99) #x0000000000013166))
(constraint (= (f #xFFFFFFFFFFFEC021) #x0000000000013FDE))
(constraint (= (f #xFFFFFFFFFFFE8E01) #x00000000000171FE))
(constraint (= (f #x40ECC6C75625AB7C) #x40ECC6C75625AB7D))
(constraint (= (f #x1F151F3556923103) #x03E2A3E6AAD24620))
(constraint (= (f #xD1DF28F4F9A95D4F) #x0A3BE51E9F352BA9))
(constraint (= (f #xCFD49BE4A327ACBB) #x09FA937C9464F597))
(constraint (= (f #x46C0D26C182704CB) #x08D81A4D8304E099))
(constraint (= (f #x5AEA4807FA822B56) #x0B5D4900FF50456A))
(constraint (= (f #x2DCCCB2B559E1CD6) #x05B999656AB3C39A))
(constraint (= (f #x1ECF887F5254D214) #x1ECF887F5254D215))
(constraint (= (f #xE7FA8F3A026E75CC) #xE7FA8F3A026E75CD))
(constraint (= (f #x661F0668A74FE623) #x0CC3E0CD14E9FCC4))
(constraint (= (f #xFFFFFFFFFFFE1459) #x000000000001EBA6))
(constraint (= (f #x0EE6C21CA4711EF1) #x0EE6C21CA4711EF0))
(constraint (= (f #xFFFF000000000002) #x0000000000000000))
(constraint (= (f #xF73380D67D640BCA) #x0EE6701ACFAC8179))
(constraint (= (f #x09499022698C6EAE) #x012932044D318DD5))
(constraint (= (f #xDEBE679559E238AA) #x0BD7CCF2AB3C4715))
(constraint (= (f #x1562B80AFF2F4A66) #x02AC57015FE5E94C))
(constraint (= (f #xC47AF0EAE160D6A6) #x088F5E1D5C2C1AD4))
(constraint (= (f #x11BB9B0EE5F43A86) #x02377361DCBE8750))
(constraint (= (f #x79C552A0CD08825A) #x0F38AA5419A1104B))
(constraint (= (f #x64600F447B05D7E2) #x0C8C01E88F60BAFC))
(constraint (= (f #x91675A588D8F4846) #x022CEB4B11B1E908))
(constraint (= (f #xDEFDB0AC6D4A9D82) #x0BDFB6158DA953B0))
(constraint (= (f #xFFFFFFFFFFFE5AEC) #x000000000001A513))
(constraint (= (f #xFFFFFFFFFFFE41F7) #x0FFFFFFFFFFFC83E))
(constraint (= (f #xFFFFFFFFFFFE447A) #x0FFFFFFFFFFFC88F))
(constraint (= (f #x0000000000000000) #x0000000000000000))
(constraint (= (f #x9AA5210313A42C18) #x9AA5210313A42C19))
(check-synth)
