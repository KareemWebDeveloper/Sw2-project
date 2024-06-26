package com.app.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.config.AppConstants;
import com.app.entites.Address;
import com.app.entites.Cart;
import com.app.entites.CartItem;
import com.app.entites.Role;
import com.app.entites.User;
import com.app.exceptions.APIException;
import com.app.exceptions.ResourceNotFoundException;
import com.app.payloads.AddressDTO;
import com.app.payloads.CartDTO;
import com.app.payloads.ProductDTO;
import com.app.payloads.UserDTO;
import com.app.repositories.AddressRepo;
import com.app.repositories.RoleRepo;
import com.app.repositories.UserRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private AddressRepo addressRepo;

	@Autowired
	private CartService cartService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;

	public UserDTO registerUser(UserDTO userDTO) {

		try {
			User user = modelMapper.map(userDTO, User.class);

			Cart cart = new Cart();
			user.setCart(cart);

			Role role = roleRepo.findById(AppConstants.USER_ID).get();
			user.getRoles().add(role);

			String country = userDTO.getAddress().getCountry();
			String state = userDTO.getAddress().getState();
			String city = userDTO.getAddress().getCity();
			String pincode = userDTO.getAddress().getPincode();
			String street = userDTO.getAddress().getStreet();
			String buildingName = userDTO.getAddress().getBuildingName();

			Address address = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(country, state,
					city, pincode, street, buildingName);

			if (address == null) {
				address = new Address(country, state, city, pincode, street, buildingName);

				address = addressRepo.save(address);
			}

			user.setAddresses(List.of(address));

			User registeredUser = userRepo.save(user);

			cart.setUser(registeredUser);

			userDTO = modelMapper.map(registeredUser, UserDTO.class);

			userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

			return userDTO;
		} catch (DataIntegrityViolationException e) {
			throw new APIException("User already exists with emailId: " + userDTO.getEmail());
		}

	}

	@Override
	public UserDTO getUserById(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
		CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);
		List<ProductDTO> products = user.getCart().getCartItems().stream()
				.map(item -> modelMapper.map(item.getProduct(), ProductDTO.class)).collect(Collectors.toList());
		userDTO.setCart(cart);
		userDTO.getCart().setProducts(products);
		return userDTO;
	}

	@Override
	public UserDTO updateUser(Long userId, UserDTO userDTO) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		String encodedPass = passwordEncoder.encode(userDTO.getPassword());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setMobileNumber(userDTO.getMobileNumber());
		user.setEmail(userDTO.getEmail());
		user.setPassword(encodedPass);
		if (userDTO.getAddress() != null) {
			String country = userDTO.getAddress().getCountry();
			String state = userDTO.getAddress().getState();
			String city = userDTO.getAddress().getCity();
			String pincode = userDTO.getAddress().getPincode();
			String street = userDTO.getAddress().getStreet();
			String buildingName = userDTO.getAddress().getBuildingName();
			Address address = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(country, state,
					city, pincode, street, buildingName);
			if (address == null) {
				address = new Address(country, state, city, pincode, street, buildingName);
				address = addressRepo.save(address);
				user.setAddresses(List.of(address));
			}
		}

		userDTO = modelMapper.map(user, UserDTO.class);
		userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
		CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);
		List<ProductDTO> products = user.getCart().getCartItems().stream()
				.map(item -> modelMapper.map(item.getProduct(), ProductDTO.class)).collect(Collectors.toList());
		userDTO.setCart(cart);
		userDTO.getCart().setProducts(products);
		return userDTO;
	}

	@Override
	public String deleteUser(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		List<CartItem> cartItems = user.getCart().getCartItems();
		Long cartId = user.getCart().getCartId();
		cartItems.forEach(item -> {
			Long productId = item.getProduct().getProductId();
			cartService.deleteProductFromCart(cartId, productId);
		});
		userRepo.delete(user);
		return "User with userId " + userId + " deleted successfully!!!";
	}

}
