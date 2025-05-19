მარტივი Ecommerce Spring boot აპლიკაცია, რომელიც დაფუძნებულია Maven-ზე, Java SE 17, Swagger UI-ზე და არ იყენებს ბაზას (SQL, JPA).
E-commerce აპლიკაცია:
(ვიყენებ Spring boot, maven, (swagger UI, java SE 17,).  
(არ ვიყენებ Lombock ან Record ხელსაწყოებს, არ ვიყენებთ რაიმე სახის ბაზას რადგან არ არის საჭირო მონაცემები ინახებოდეს.)
1.	უნდა არსებობდეს მხოლოდ ორი იუზერის ტიპი (user; admin)
2.	USER-ს უნდა შეეძლოს მხოლოდ: პროდუქტის კალათში დამატება(add to cart), პროდუქტის კალათიდან წაშლა(remove from cart), კალათში დამატებული პროდუქტის შეძენა (check out), კალათში დამატების გარეშე პირდაპირ შეძენა (checkout now)
3.	კალათის ფუნქციები:

	A. პროდუქტის დამატება კალათში სასურველი რაოდენობით(მოწმდება მარაგში არის თუ არა პროდუქტი);

	B. პროდუქტის წაშლა კალათიდან სასურველი რაოდენობით;

4.	პროდუქტის შეძენის ოფციები:
	
	A. Checkout Now (კალათში დამატების გარეშე პირდაპირ შეძენა)

	B.კალათში დამატებული ყველა  პროდუქტის შეძენა ერთდროულად;

	C. კალათში დამატებული პროდუქტებიდან ცალკეულის შეძენა;

5.	USER-ს ექნება ბიუჯეტის ლიმიტი 1000 GEL;
	
6.	იუზერმა ყოველ რექუესტზე უნდა გამოაგზავნოს თავისი სახელი ADMIN ან USER რომ მიეცეს შესაბამისი დაშვება მანიპულაციებზე (თუ ადმინია მხოლოდ პროდუქტებს ამატებს, ცვლის ან შლის; თუ მომხმარებელია მხოლოდ პროდუქტებს შეიძენს.)

7.	მომხმარებლის მიერ ყოველი პროდუქტის შეძენის მცდელობისას აპლიკაციაში უნდა მოწმდებოდეს პროდუქტის მარაგი(ხომ არ ამოიწურა რაოდენობა) და ბიუჯეტი(ხომ არ გადააჭარბა 1000 ლარს სასურველი პროდუქტების ღირებულებამ)

8.	ADMIN-ს უნდა შეეძლოს მხოლოდ პროდუქტების მენეიჯინგი: დამატება, შეცვლა, წაშლა.
   
9.	პროდუქტს აუცილებლად უნდა გააჩნდეს სახელი, ფასი, მარაგი, id(id მიენიჭება ადმინის მიერ პროდუქტის შექმნისას);
  
10.	ვიცავთ SOLID პრინციპებს;
    
11.	აპლიკაცია უნდა იყოს მდგრადი და Reusable, ცვლილებისათვის მარტივი, რათა მომავალში შევძლო სხვადასხვა features დამატება(Spring JPA, front-end და სხვა...) და აპლიკაციის დახვეწა.

✅ service/admin/
AdminService — ინტერფეისი

AdminServiceImpl — პროდუქტის მენეჯმენტის პასუხისმგებელი

✅ service/product/
ProductService — ინტერფეისი

ProductServiceImpl — პროდუქტის CRUD ოპერაციები

✅ service/user/
CartService — კალათის ლოგიკა

PurchaseService — ყიდვის პროცესების ორგანიზატორი

✅ service/user/strategy/ (Strategy Pattern)
CheckoutStrategy — ინტერფეისი

DirectCheckoutStrategy — კალათის გარეშე ყიდვა

AllCartCheckoutStrategy — კალათის მთლიანად ყიდვა

SingleCartItemCheckoutStrategy — კონკრეტული ელემენტის ყიდვა კალათიდან

✅ util/
RoleValidator — ავტორიზაცია

BudgetValidator — ბიუჯეტის შემოწმება

✅ model/enums/
Role — ADMIN / USER ენამი

✅ controller/
AdminController — პროდუქტების მართვა (მხოლოდ ADMIN)

UserController — კალათა და ყიდვა (მხოლოდ USER)

✅ model/
Product — პროდუქტის მოდელი (id, name, price, stock)

CartItem — კალათის ელემენტი (product, quantity)

UserSession — იუზერის მდგომარეობა (username, role, budget, cart)

✅ dto/
ProductDto — პროდუქტის გადაცემა კონტროლერისთვის

PurchaseRequestDto — ყიდვის მოთხოვნა

ApiResponse — სტანდარტული response-ის ობიექტი

src/
├── main/
│   ├── java/
│   │   └── com/example/ecommerce/
│   │       ├── EcommerceApplication.java
│   │       ├── config/
│   │       │   └── SwaggerConfig.java
│   │       ├── controller/
│   │       │   ├── AdminController.java
│   │       │   └── UserController.java
│   │       ├── dto/
│   │       │   ├── ProductDto.java
│   │       │   ├── PurchaseRequestDto.java
│   │       │   └── ApiResponse.java
│   │       ├── model/
│   │       │   ├── Product.java
│   │       │   ├── CartItem.java
│   │       │   ├── UserSession.java
│   │       │   └── enums/
│   │       │       └── Role.java
│   │       ├── service/
│   │       │   ├── admin/
│   │       │   │   ├── AdminService.java
│   │       │   │   └── AdminServiceImpl.java
│   │       │   ├── user/
│   │       │   │   ├── CartService.java
│   │       │   │   ├── PurchaseService.java
│   │       │   │   ├── strategy/
│   │       │   │   │   ├── CheckoutStrategy.java
│   │       │   │   │   ├── DirectCheckoutStrategy.java
│   │       │   │   │   ├── AllCartCheckoutStrategy.java
│   │       │   │   │   └── SingleCartItemCheckoutStrategy.java
│   │       │   ├── product/
│   │       │   │   ├── ProductService.java
│   │       │   │   └── ProductServiceImpl.java
│   │       ├── util/
│   │       │   ├── RoleValidator.java
│   │       │   └── BudgetValidator.java
│   └── resources/
│       └── application.properties

